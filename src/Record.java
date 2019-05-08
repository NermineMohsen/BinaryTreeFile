import java.io.IOException;
import java.io.RandomAccessFile;

public class Record {
    int recsiz=16;
    static RandomAccessFile datafile;
    int value,offset,left,right;
    public Record leftRec,rightRec;
    Record(RandomAccessFile file){datafile=file;}
    Record(int v, int o, int l, int r){
        value=v;
        offset=o;
        left=l;
        right=r;
    }
    public Record getleft(){
        if (left==-1){return  null;}
        try {
            datafile.seek(left*recsiz);
            return readRec();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Record getright(){
        if (right==-1){return  null;}

        try {
            datafile.seek(right*recsiz);
            return readRec();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    static Record readRec(){
        try {
            return new Record(datafile.readInt(),datafile.readInt(),datafile.readInt(),datafile.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    static void writeRec(int val, int offset){
        try {

            datafile.writeInt(val);
            datafile.writeInt(offset);
            datafile.writeInt(-1);
            datafile.writeInt(-1);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    static void writeEmptyRec(int val){
        try {

            datafile.writeInt(val);
            datafile.writeInt(0);
            datafile.writeInt(0);
            datafile.writeInt(0);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    static void writeRec(Record a){
        try {

            datafile.writeInt(a.value);
            datafile.writeInt(a.offset);
            datafile.writeInt(-1);
            datafile.writeInt(-1);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    static void printRec(int indx){
        Record a=readRec();
        if (a!=null) {
            System.out.format("%10s %10s %10s %10s %10s", indx, a.value, a.offset, a.left, a.right);
            System.out.println();
        }
    }
}
