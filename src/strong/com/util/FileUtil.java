package strong.com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
	public static void copyFile(String oldPath, String newPath)
			throws IOException {
		//int bytesum = 0;
		int byteread = 0;
		File oldfile = new File(oldPath);
		if (oldfile.exists()) {
			InputStream inStream = new FileInputStream(oldPath); // 
			File desFile = new File(newPath);
			if(!desFile.exists()){
				desFile.mkdirs();
			}
			FileOutputStream fs = new FileOutputStream(newPath+File.separator+oldfile.getName());
			byte[] buffer = new byte[4096];
			//int length;
			while ((byteread = inStream.read(buffer)) != -1) {
				//bytesum += byteread; // 
				fs.write(buffer, 0, byteread);
			}
			fs.close();
			inStream.close();
		}
	}
}
