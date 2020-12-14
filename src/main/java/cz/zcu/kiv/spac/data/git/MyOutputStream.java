package cz.zcu.kiv.spac.data.git;

import java.io.OutputStream;

public class MyOutputStream extends OutputStream {

    StringBuilder anotatedText;

    public MyOutputStream() {
        anotatedText = new StringBuilder();
    }

    @Override
    public void write(int b) {
        int[] bytes = {b};
        write(bytes, 0, bytes.length);
    }

    public void write(int[] bytes, int offset, int length) {
        String s = new String(bytes, offset, length);
        anotatedText.append(s);
    }

    public void myPrint() {
        System.out.println(anotatedText);
    }
}
