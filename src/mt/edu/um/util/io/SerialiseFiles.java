package mt.edu.um.util.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerialiseFiles {

	public static void serialize(Serializable serializable, String filename)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(serializable);
		out.close();
	}

	public static <T extends Serializable> T deserialize(String filename,
			Class<T> cls) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream in = new ObjectInputStream(fis);
		Object object = in.readObject();
		T result = cls.cast(object);
		return result;
	}

}
