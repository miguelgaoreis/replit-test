package tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectIO {
    private static final String filepath="src\\tracks\\singlePlayer\\Files\\";
    private static final String result_filepath="Results/";


    public void WriteObjectToFile(Object serObj, String name) {

        try {
            FileOutputStream fileOut = new FileOutputStream(filepath + name + "Stats.txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();
//                System.out.println("The Object  was succesfully written to a file");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ResultWriteObjectToFile(Object serObj, String name) {

        try {
            FileOutputStream fileOut = new FileOutputStream(result_filepath + name + "Stats.txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();
//                System.out.println("The Object  was succesfully written to a file");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object ReadObjectFromFile(String name) {

        try {

            FileInputStream fileIn = new FileInputStream(filepath + name );
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

//                System.out.println("The Object has been read from the file");
            objectIn.close();
            return obj;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public Object ResultReadObjectFromFile(String name) {

        try {

            FileInputStream fileIn = new FileInputStream(result_filepath + name );
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

//                System.out.println("The Object has been read from the file");
            objectIn.close();
            return obj;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

