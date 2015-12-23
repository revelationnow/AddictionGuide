package me.minichro.addictionguide;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class AppendingObjectOutputStream extends ObjectOutputStream {

    public AppendingObjectOutputStream(OutputStream out) throws IOException{

            super(out);

    }

    @Override
    protected void writeStreamHeader() {

        try {
            // do not write a header, but reset:
            // this line added after another question
            // showed a problem with the original
            reset();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
