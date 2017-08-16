package accessDatabase;

import org.fusesource.lmdbjni.*;

import java.io.File;

public class accessDb {
    public void loadDbFile(File data) throws Exception{
        if (data == null)
            System.out.println("File is not found.");
        System.out.println("Database " + data +" is loaded...");

        // opening the database
        Env env = new Env("/data/mydb/db-kb/concepts/data.mdb");
        Database db = env.openDatabase();

        // iterating the database
        Transaction tx = env.createReadTransaction();
        EntryIterator it = db.iterate(tx);

        for (Entry next : it.iterable()){
            System.out.println(next);
        }

        tx.abort();

    }
}
