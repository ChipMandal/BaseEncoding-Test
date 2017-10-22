package com.chipmandal.encoding;

import java.util.Arrays;

public class Base94 implements BaseEncoding{

    private final char[] alphabet;
    private final byte[] reverse;

    /**
     * If you want to use a different alphabet.
     * An alphabet is 94 distinct bytes each in the range 0x21 to 0x126
     * @param useAlphabet
     */
    public Base94(char[] useAlphabet) {

        if ( useAlphabet.length == 94 ) {
            char[] chekbytes = new char[128];
            for ( int i=0; i <  useAlphabet.length; i++) {
                if ( useAlphabet[i] > 32 && useAlphabet[i] < 127 && chekbytes[useAlphabet[i]] == 0) {
                    chekbytes[useAlphabet[i]] = 1;
                } else {
                    throw new IllegalArgumentException("Invalid or duplicate byte in alphabet " + String.valueOf(useAlphabet[i]));
                }
            }
        } else {
            throw new IllegalArgumentException("Size of alphabet has to be 94");
        }

        alphabet = Arrays.copyOf(useAlphabet, 94);
        reverse = new byte[127];
        for ( int i = 0; i < alphabet.length; i++) {
            reverse[alphabet[i]] = (byte) i;
        }
    }


    @Override
    public byte[] encode(byte[] input) {
        return encodePrivate1(input);
    }

    /**
     * Handcoded bit operations.
     *
     * input[0] [7-3] -> output[0] [4-0]
     * input[0] [2-0] -> ouput[1] [7-5]
     * input[1] [7-3] -> output[1] [4-0]
     * input[1] [2-0] -> output[2] [4-2]
     * input[2] [7-6] -> output[2] [1-0]
     * input[2] [5-0] -> output[3] [7-2]
     * input[3] [7-6] -> output[3] [1-0]
     * input[3] [5-1] -> output[4] [4-0]
     * input[3] [0]   -> output[5] [7]
     * input[4] [7-1] -> output[5] [6-0]
     * input[4] [0]   -> output[6] [4]
     *
     *
     *
     */
    private byte[] encodePrivate0(byte[] input) {
        if ( input == null || input.length == 0 ) {
            return new byte[0];
        }
        int outputBytes = ((input.length * 8) / 13) * 2 ;
        int mod = (input.length * 8) % 13;
        int add = 0;
        if (  mod > 0 && mod < 7 ) {
            add=1;
        } else if( mod >= 7){
            add=2;
        }
        outputBytes += add;
        byte[] output = new byte[outputBytes];

        int writebyteNum = 0;
        for  (int readByteNum =0;  readByteNum < input.length-2; readByteNum++) {
            switch (readByteNum % 13) { //Is mod better or saving state?
                case 0:
                    output[writebyteNum] = (byte) (input[readByteNum] >>> 3);
                    output[++writebyteNum] = (byte) (input[readByteNum] << 5);
                    break;
                case 1:
                    output[writebyteNum] |= (byte) (input[readByteNum] >>> 3);
                    output[++writebyteNum] = (byte) ((input[readByteNum] << 2) & 0x1F);
                    break;
                case 2:
                    output[writebyteNum] |= (byte) (input[readByteNum] >>> 6);
                    output[++writebyteNum] = (byte) (input[readByteNum] << 2);
                    break;
                case 3:
                    output[writebyteNum] |= (byte) (input[readByteNum] >>> 6);
                    output[++writebyteNum] = (byte) ((input[readByteNum] >>> 1) & 0x1F);
                    output[++writebyteNum] = (byte) (input[readByteNum] << 7);
                    break;
                case 4:
                    output[writebyteNum] |= (input[readByteNum] >>> 1);
                    output[++writebyteNum] = (byte) ( (input[readByteNum] << 4) & 0x1F);
                    break;
                case 5:
                    output[writebyteNum] |= (input[readByteNum] >>> 4);
                    output[++writebyteNum] = (byte) (input[readByteNum] << 4);
                    break;
                case 6:
                    output[writebyteNum] |= (input[readByteNum] >>> 4);
                    output[++writebyteNum] = (byte) ((input[readByteNum] << 1) & 0x1F);
                    break;
                case 7:
                    output[writebyteNum] |= (input[readByteNum] >>> 7);
                    output[++writebyteNum] = (byte) (input[readByteNum] << 1);
                    break;
                case 8:
                    output[writebyteNum] |= (input[readByteNum] >>> 7);
                    output[++writebyteNum] = (byte) ((input[readByteNum] >> 2) & 0x1F);
                    output[++writebyteNum] = (byte) (input[readByteNum] << 6);
                    break;
                case 9:
                    output[writebyteNum] |= (input[readByteNum] >>> 2);
                    output[++writebyteNum] = (byte) ((input[readByteNum] << 3) & 0x1F);
                    break;
                case 10:
                    output[writebyteNum] |= (input[readByteNum] >>> 5);
                    output[++writebyteNum] = (byte) (input[readByteNum] << 3);
                    break;
                case 11:
                    output[writebyteNum] |= (input[readByteNum] >>> 5);
                    output[++writebyteNum] = (byte) (input[readByteNum] & 0x1F);
                    break;
                case 12:
                    output[++writebyteNum] = input[readByteNum];
                    writebyteNum++;
                    break;
            }
        }
        byte penultimateByte = input[input.length -2];
        switch ( (input.length-2) % 13) { //Is mod better or saving state?
            case 0:
                output[writebyteNum] = (byte) (input[input.length-2] >>> 3);
                output[++writebyteNum] = (byte) ((input[input.length-2] << 5)  | (input[input.length-1] >>> 3));
                //Remainder 3 bits-> to be put in 1 byte
                output[++writebyteNum] = (byte) (input[input.length-1] & 0x07);
                break;
            case 1:
                output[writebyteNum] |= (byte) (input[input.length-2] >>> 3);
                //Remainder 11 -> 11 bits to be written to 2 bytes
                output[++writebyteNum] = (byte) (input[input.length-2]  & 0x07);
                output[++writebyteNum] = input[input.length-1];
                break;
            case 2:
                output[writebyteNum] |= (byte) (input[input.length-2] >>> 6);
                output[++writebyteNum] = (byte) (input[input.length-2] << 2);
                output[writebyteNum] |= (byte) (input[input.length-1] >>> 6);
                //Remainder 6
                output[++writebyteNum] = (byte) (input[input.length-1] & 0x3F);
                break;
            case 3:
                output[writebyteNum] |= (byte) (input[input.length-2] >>> 6);
                output[++writebyteNum] = (byte) ((input[input.length-2] >>> 1) & 0x1F);
                output[++writebyteNum] = (byte) (input[input.length-2] << 7);
                output[writebyteNum] |= (byte) (input[input.length-1] >>> 1);
                //1 remain
                output[++writebyteNum] = (byte) (input[input.length-1] & 0x01);
                break;
            case 4:
                output[writebyteNum] |= (input[input.length-2] >>> 1);
                //Remaining 9
                output[++writebyteNum] = (byte) (input[input.length-2] & 0x01);
                output[++writebyteNum] = input[input.length-1];
                break;
            case 5:
                output[writebyteNum] |= (input[input.length-2] >>> 4);
                output[++writebyteNum] = (byte) (input[input.length-2] << 4);
                output[writebyteNum] |= (input[input.length-1] >>> 4);
                //Remaining 4 will be written as single byte
                output[++writebyteNum] = (byte) (input[input.length-1] &0xF);
                break;
            case 6:
                output[writebyteNum] |= (input[input.length-2] >>> 4);
                //Remaining 12 -> write to 2 bytes
                output[++writebyteNum] = (byte) (input[input.length-2]  & 0xF);
                output[++writebyteNum] = input[input.length-1] ;
                break;
            case 7:
                output[writebyteNum] |= (input[input.length-2] >>> 7);
                output[++writebyteNum] = (byte) (input[input.length-2] << 1);
                output[writebyteNum] |= (input[input.length-1] >>> 7);
                //Remaining 7 -> have to write to 2 bytes
                output[++writebyteNum] = 0;
                output[++writebyteNum] = (byte) (input[input.length-1] & 0x7F);
                break;
            case 8:
                output[writebyteNum] |= (input[input.length-2] >>> 7);
                output[++writebyteNum] = (byte) ((input[input.length-2] >> 2) & 0x1F);
                output[++writebyteNum] = (byte) (input[input.length-2] << 6);
                output[writebyteNum] |= (input[input.length-1] >>> 2);
                //Remaining 2 -> one byte
                output[++writebyteNum] = (byte) (input[input.length-1] & 0x03);
                break;
            case 9:
                output[writebyteNum] |= (input[input.length-2] >>> 2);
                //Remaining 10
                output[++writebyteNum] = (byte) (input[input.length-2]  & 0x03);
                output[++writebyteNum] = input[input.length-1];
                break;
            case 10:
                output[writebyteNum] |= (input[input.length-2] >>> 5);
                output[++writebyteNum] = (byte) (input[input.length-2] << 3);
                output[writebyteNum] |= (input[input.length-1] >>> 5);
                //Remaining 5
                output[++writebyteNum] = (byte) (input[input.length-1] & 0x1F);
                break;
            case 11:
                output[writebyteNum] |= (input[input.length-2] >>> 5);
                output[++writebyteNum] = (byte) (input[input.length-2] & 0x1F);
                output[++writebyteNum] = (input[input.length-1]);
                break;
            case 12:
                output[++writebyteNum] = input[input.length-2];
                //Reaminder -> 8
                output[++writebyteNum] = input[input.length-1];
                break;
        }

        return output;
    }

    private byte[] encodePrivate1(byte[] input) {
        if ( input == null || input.length == 0 ) {
            return new byte[0];
        }
        int outputBytes = ((input.length * 8) / 13)*2;
        int mod = (input.length * 8) % 13;
        int rem =  (mod <=6) ? 1 : 2;

        byte[] output = new byte[outputBytes+rem];

        int readPos = 7;
        int readByte = 0;
        int byteCount =0;
        for ( ; byteCount < outputBytes ; byteCount+=2) {
            //Read the next five bytes and put it in [4-0]
            if ( readPos >= 4) {
                //We have more than 4 in the current readbyte
                output[byteCount] = (byte) (( (input[readByte] & 0xFF) >>> (readPos-4)) & 0x1F);
                readPos = readPos - 5;
                if ( readPos < 0 ) {
                    readPos = 7;
                    readByte++;
                }
            } else {
                //We have less then 4, so have to take partially from the next byte
                output[byteCount] = (byte) (((input[readByte] << (4-readPos)) & 0x1F) |
                                                    ( (input[readByte+1] & 0xFF) >>> (4+readPos)));

                readByte++;
                readPos = 3+readPos;
            }
            //Read the next 8 bytes
            if (readPos == 7 ) {
                output[byteCount+1] = input[readByte];
            } else {
                output[byteCount+1] = (byte) ((input[readByte] << (7-readPos)) |
                                                ( (input[readByte+1] & 0xFF) >>> (readPos+1)));
            }
            readByte++;
        }

        if (mod == 0 ) {
            output[outputBytes] = 0;
        } else if ( mod <= 6) {
            output[outputBytes] = (byte) (input[input.length-1] & 0x3F);
        } else if (mod <= 8){
            output[outputBytes] =  0;
            output[outputBytes+1] = input[input.length-1];
        } else {
            output[outputBytes] = (byte) (input[input.length-2] & 0x0F);
            output[outputBytes+1] = input[input.length-1];
        }
        return output;
    }

    @Override
    public String encodeString(byte[] input) {
        return bytesToString(encode(input));
    }

    private String bytesToString(byte[] encode) {
        StringBuilder builder = new StringBuilder();
        int evenLength = encode.length / 2;

        for ( int i = 0; i + 1 < encode.length; i +=2 ) {
            int val = encode[i] << 8 | (encode[i+1] & 0xFF) ;
            int first = val  % 94;
            int second = val / 94;
            builder.append(alphabet[second]);
            builder.append(alphabet[first]);
        }
        if ( encode.length  %2 ==1 ) {
            //encode the last byte
            builder.append(alphabet[encode[encode.length-1]]);
        }

        return builder.toString();
    }


    @Override
    public byte[] decode(byte[] input) {
        return decode1(input);
    }

    public byte[] decode0(byte[] input) {
        if ( input == null || input.length == 0 ) {
            return new byte[0];
        }
        int extraDigits = input.length % 2 == 0 ? 2 : 1;
        int evenLength = input.length - extraDigits ;
        int numbits = (evenLength/2) * 13;

        int outputLength = (numbits / 8 + (numbits%8 == 0 ? 0 : 1));
        if ( extraDigits == 2 && ( numbits % 8 >= 4 || numbits %8 == 0) ) {
            outputLength++;
        }
        byte[] output = new byte[outputLength];
        int writePos = 7;
        int writeByte = 0;
        for (int i =0; i < evenLength; i+= 2) {
            //Write the first byte
            if ( writePos >= 4) {
                output[writeByte] |= (input[i] << (writePos - 4));
                writePos = writePos - 5;
                if ( writePos < 0) {
                    writeByte++;
                    writePos = 7;
                }
            } else {
                output[writeByte] |= ((input[i] & 0xFF) >>> (4-writePos));
                output[writeByte+1] |= (input[i]  << (4+writePos));
                writeByte++;
                writePos = 3+writePos;
            }

            //2nd byte
            output[writeByte] |= ((input[i+1] & 0xFF) >>> (7-writePos));
            if ( writeByte + 1 < outputLength ) {
                //This condition is only needed in the special case where the bytes
                // are exactly divisible by 8. In this case we would not write the last byte.
                // TODO for efficency code this as a different function.
                output[writeByte + 1] |= (input[i + 1] << (writePos + 1));
            }

            writeByte++;
        }


        if ( writeByte == outputLength - 1) {
            output[writeByte] |= (input[input.length-1] & ( 0xFF >>> (7-writePos)));
        } else if(writeByte == outputLength - 2) {
            output[writeByte] |= (input[input.length-2] & ( 0xFF >>> (7-writePos)));
            output[writeByte+1] = input[input.length -1];
        }

        return output;
    }


    public byte[] decode1(byte[] input) {
        if ( input == null || input.length == 0 ) {
            return new byte[0];
        }
        int extraDigits = input.length % 2 == 0 ? 2 : 1;
        int evenLength = input.length - extraDigits ;
        int numbits = (evenLength/2) * 13;

        if ( numbits % 8 == 0 && extraDigits == 1) {
            return decode1MultipleOf8(input, numbits, evenLength);
        } else {
            return decode1NotMultipleOf8(input, numbits, extraDigits, evenLength);
        }

    }

    public byte[] decode1NotMultipleOf8(byte[] input, int numbits, int extraDigits, int evenLength) {

        // case 1 : numbits%8 == 0 && extradigits = 2
            // x + 0  + 1 = x+1
        // case 2 : numbits%8 != 0 && extradigits = 2
            // x + 1 + numbits %8 >= 4
        // case 3 : numbits%8 != 0 && extradigits = 1
        // x + 1

        int outputLength = (numbits / 8 + 1);
        if ( extraDigits == 2 && numbits % 8 >= 4 ) {
            outputLength++;
        }
        byte[] output = new byte[outputLength];
        int writePos = 7;
        int writeByte = 0;
        for (int i =0; i < evenLength; i+= 2) {
            //Write the first byte
            if ( writePos >= 4) {
                output[writeByte] |= (input[i] << (writePos - 4));
                writePos = writePos - 5;
                if ( writePos < 0) {
                    writeByte++;
                    writePos = 7;
                }
            } else {
                output[writeByte] |= ((input[i] & 0xFF) >>> (4-writePos));
                output[writeByte+1] |= (input[i]  << (4+writePos));
                writeByte++;
                writePos = 3+writePos;
            }

            //2nd byte
            output[writeByte] |= ((input[i+1] & 0xFF) >>> (7-writePos));
            output[writeByte + 1] |= (input[i + 1] << (writePos + 1));


            writeByte++;
        }


        if ( writeByte == outputLength - 1) {
            output[writeByte] |= (input[input.length-1] & ( 0xFF >>> (7-writePos)));
        } else if(writeByte == outputLength - 2) {
            output[writeByte] |= (input[input.length-2] & ( 0xFF >>> (7-writePos)));
            output[writeByte+1] = input[input.length -1];
        }

        return output;
    }
    public byte[] decode1MultipleOf8(byte[] input, int numbits, int evenLength) {
        int outputLength = (numbits / 8 );

        byte[] output = new byte[outputLength];
        int writePos = 7;
        int writeByte = 0;
        int i = 0;
        for ( ; i < evenLength-2; i+= 2) {
            //Write the first byte
            if ( writePos >= 4) {
                output[writeByte] |= (input[i] << (writePos - 4));
                writePos = writePos - 5;
                if ( writePos < 0) {
                    writeByte++;
                    writePos = 7;
                }
            } else {
                output[writeByte] |= ((input[i] & 0xFF) >>> (4-writePos));
                output[writeByte+1] |= (input[i]  << (4+writePos));
                writeByte++;
                writePos = 3+writePos;
            }

            //2nd byte
            output[writeByte] |= ((input[i+1] & 0xFF) >>> (7-writePos));
            output[writeByte + 1] |= (input[i + 1] << (writePos + 1));
            writeByte++;
        }
        if ( writePos >= 4) {
            output[writeByte] |= (input[i] << (writePos - 4));
            writePos = writePos - 5;
            if ( writePos < 0) {
                writeByte++;
                writePos = 7;
            }
        } else {
            output[writeByte] |= ((input[i] & 0xFF) >>> (4-writePos));
            output[writeByte+1] |= (input[i]  << (4+writePos));
            writeByte++;
            writePos = 3+writePos;
        }

        //2nd byte
        output[writeByte] |= ((input[i+1] & 0xFF) >>> (7-writePos));
        return output;
    }

    @Override
    public byte[] decodeString(String input) {
        char[] array = input.toCharArray();

        byte[] output = new byte[array.length];
        for ( int i = 0; i + 1 < array.length; i+= 2 ) {
            if ( array[i] < 33 || array[i] > 126 || array[i+1] < 33 || array[i+1] > 126 ) {
                throw new IllegalArgumentException("Illegal character");
            }
            int val = reverse[array[i]] * 94 + reverse[array[i+1]];
            output[i+1] = (byte) (val & 0xFF);
            output[i] = (byte) (val >>> 8);
        }
        if ( array.length %2 == 1 ) {
            output[array.length-1] = reverse[array[array.length-1]];
        }
        return decode(output);
    }
}
