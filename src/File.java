import jdk.swing.interop.SwingInterOpUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class File {
    RandomAccessFile datafile;
    public int limit;
    int empty;
    int recsiz = 16;

    File() {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter your nmber of records: ");
        limit = in.nextInt();
        System.out.println("Enter file name: ");
        String Filename = in.next();
        CreateRecordsFile(Filename, limit);
    }

    public void CreateRecordsFile(String filename, int numberOfRecords) {
        try {
            datafile = new RandomAccessFile(filename + ".bin", "rw");
            Record.datafile = datafile;
            for (int counter = 1; counter < numberOfRecords; counter++) {
                datafile.writeInt(counter);
                datafile.writeInt(0);
                datafile.writeInt(0);
                datafile.writeInt(0);
            }
            datafile.writeInt(-1);
            datafile.writeInt(0);
            datafile.writeInt(0);
            datafile.writeInt(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int InsertNewRecordAtIndex(String filename, int Key, int ByteOffset) {
        try {
            datafile.seek(0);
            empty = datafile.readInt();
            if (empty != -1) {
                datafile.seek(recsiz * empty);
                Record.writeRec(Key, ByteOffset);
            } else {
                return -1;
            }
            if (empty != -1) {
                datafile.seek(0);
                if (empty + 1 < limit) {
                    datafile.writeInt(empty + 1);
                } else if (empty + 1 == limit) {
                    datafile.writeInt(-1);
                }
                datafile.seek(1 * recsiz);
                //// add root
                insertRec(1, Record.readRec(), Key, empty + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return empty + 1;
    }

    Record insertRec(int pos, Record root, int key, int newpos) {
        try {
            if (key < root.value) {
                if (root.left == -1) { //left is empty
                    datafile.seek(pos * recsiz + 2 * 4);
                    datafile.writeInt(newpos - 1);
                    return root;
                }
                root.leftRec = insertRec(root.left, root.getleft(), key, newpos);
            } else if (key > root.value) {
                if (root.right == -1) { //right is empty
                    datafile.seek(pos * recsiz + 3 * 4);
                    datafile.writeInt(newpos - 1);
                    return root;
                }
               root.rightRec = insertRec(root.right, root.getright(), key, newpos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    Record Binarysearch(int pos, Record root, int key) {
        if (root==null){return null;}
        if (root.value == key) {
            //   System.out.println("found with offset: "+root.offset);
            return root;
        }
        if (key < root.value) {
            if (root.left == -1) {
                return null;
            }

            return Binarysearch(root.left, root.getleft(), key);
        }
        return Binarysearch(root.right, root.getright(), key);

            /*else if (key > root.value) {
                if (root.right==-1){return null;}

            }
           */
    }

    void SearchRecordInIndex(String filename, int Key) {
        try {
            datafile.seek(recsiz);
            Record root = Record.readRec();
            if (root.left == 0) {
                System.out.println("The tree is empty");
                return;
            }
            Record finding = Binarysearch(1, root, Key);
            if (finding == null) {
                System.out.println("not found -1");
            } else {
                System.out.println("found with offset : " + finding.offset);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getfullinput() {
        Scanner in = new Scanner(System.in);
        for (int o = 1; o < limit; o++) {
            System.out.println("Enter your key: ");
            int key = in.nextInt();
            System.out.println("Enter your offset: ");
            int offset = in.nextInt();
            InsertNewRecordAtIndex("", key, offset);
            DisplayIndexFileContent("");
        }
    }

    void DeleteIndex(int indx) { //empty pointer -1
        // shift upwards and add the new empty rec
        //loop and insertrec all records to rebalance the tree
        try {
            datafile.seek(0);

            int newempty = datafile.readInt() - 1;
            if (newempty == -2) {
                newempty = limit - 1;
            }
            datafile.seek(0);
            datafile.writeInt(newempty);
            datafile.seek(recsiz * (indx));
            for (int o = indx; o< limit; o++) { //shift
                long offset = datafile.getFilePointer(); //where to write
                datafile.seek(recsiz + offset);
                //System.out.println("offset : " + recsiz + offset);
                if ((datafile.getFilePointer()/16) == limit) {
                    datafile.seek(datafile.getFilePointer() - recsiz);
                    break;
                }
                Record towrite = Record.readRec();
              //  System.out.println(towrite.value + "|" + towrite.offset);

                datafile.seek(offset);
                Record.writeRec(towrite);
            }
            for (int i=1;i<newempty;i++){
                datafile.seek(i*recsiz+8);
                datafile.writeInt(-1);
                datafile.writeInt(-1);
            }
            for (int r = newempty; r +1 < limit; r++) {
                Record.writeEmptyRec(newempty);
            }
            Record.writeEmptyRec(-1);

         //   System.out.println("NEW " + newempty);
            for (int o = 2; o < newempty; o++) {//rebalance tree
                datafile.seek(recsiz);
                Record root = Record.readRec();
                datafile.seek(o * recsiz);
                Record curr= Record.readRec();
                insertRec(1, root,curr.value, o+1);
                DisplayIndexFileContent("");
            }
            DisplayIndexFileContent("");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void DisplayIndexFileContent(String filename) {
        try {

            datafile.seek(0);
            System.out.println("-----------------------------------------------------------------------------");
            System.out.printf("%10s %10s %10s %10s %10s", "Node#", "Value", "Offset", "Left", "Right");
            System.out.println();
            System.out.println("-----------------------------------------------------------------------------");

            for (int o = 0; o < limit; o++) {
                Record.printRec(o);
            }
            System.out.println("-----------------------------------------------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void TraverseBinarySearchTreeInOrder(String FileName) {
        try {
            datafile.seek(recsiz);
            Record root = Record.readRec();
            inorder(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void inorder(Record root) {
        if (root != null) {
            inorder(root.getleft());
            System.out.println(root.value);
            inorder(root.getright());
        }

    }
}