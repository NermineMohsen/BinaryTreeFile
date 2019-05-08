import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        File x=new File();
        Scanner a=new Scanner(System.in);
        String cont="Y";
        Boolean added=false;
        while (!cont.equals("N")){
            System.out.println("1) insert record");
            System.out.println("2) Search");
            System.out.println("3) Inorder");
            System.out.println("4) Display all");
            if (!added) {
                System.out.println("5) Enter all inputs at once");
                System.out.println("6) Delete");
            }
            else {
                System.out.println("5) Delete");
            }
            cont=a.next();
            if (cont.equals("N")){break;}
            else if (cont.equals("1")){
                added=true;
                System.out.println("Enter your key: ");
                int key = a.nextInt();
                System.out.println("Enter your offset: ");
                int offset = a.nextInt();
                x.InsertNewRecordAtIndex("", key, offset);
                x.DisplayIndexFileContent("");}
            else if (cont.equals("2")){
                System.out.println("Enter your key: ");
                int key = a.nextInt();
                x.SearchRecordInIndex("",key);
            }
            else if (cont.equals("3")){
                x.TraverseBinarySearchTreeInOrder("");
            }
            else if (cont.equals("4")){
                x.DisplayIndexFileContent("");
            }
            else if (cont.equals("5")&&!added){
                x.getfullinput();
            }
            else {
                System.out.println("Enter index to delete: ");
                int indx = a.nextInt();
                if (indx==0){
                    System.out.println("not in range");
                }
                else
                x.DeleteIndex(indx);
            }

        }
    }
}
