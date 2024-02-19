package net.bigyous.gptgodmc.utils;

import java.io.OutputStream;
import java.io.IOException;
public class PCMtoWAV {
    public static byte[] get16BitPcm(short[] data) {
        byte[] resultData = new byte[2 * data.length];
        int iter = 0;
        for (short sample : data) {
            resultData[iter++] = (byte)(sample & 0x00ff);
            resultData[iter++] = (byte)((sample & 0xff00) >>> 8);
        }
        return resultData;
    }

    public static void PCMtoFile(OutputStream os, short[] pcmdata, int srate) throws IOException {
        byte[] header = new byte[44];
        byte[] data = get16BitPcm(pcmdata);
        int format = 16;
    
        long totalDataLen = data.length + 36;
        int channel = 1;

        int bitrate = format * channel * srate;
        // wav header
        header[0] = 'R'; 
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; 
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = (byte) format; 
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; 
        header[21] = 0;
        header[22] = (byte) channel; 
        header[23] = 0;
        header[24] = (byte) (srate & 0xff);
        header[25] = (byte) ((srate >> 8) & 0xff);
        header[26] = (byte) ((srate >> 16) & 0xff);
        header[27] = (byte) ((srate >> 24) & 0xff);
        header[28] = (byte) ((bitrate / 8) & 0xff);
        header[29] = (byte) (((bitrate / 8) >> 8) & 0xff);
        header[30] = (byte) (((bitrate / 8) >> 16) & 0xff);
        header[31] = (byte) (((bitrate / 8) >> 24) & 0xff);
        header[32] = (byte) ((channel * format) / 8); 
        header[33] = 0;
        header[34] = 16; 
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (data.length  & 0xff);
        header[41] = (byte) ((data.length >> 8) & 0xff);
        header[42] = (byte) ((data.length >> 16) & 0xff);
        header[43] = (byte) ((data.length >> 24) & 0xff);
    
        os.write(header, 0, 44);
        os.write(data);
        os.close();
    }

}
