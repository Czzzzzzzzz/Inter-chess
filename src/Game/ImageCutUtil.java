package Game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageCutUtil {
	final static int NUM = 25;

	public static void removeAll(File path) {
		File fileImage;
		for (int i = 0; i < NUM; i++) {
			fileImage = new File(path, i + ".jpg");
			if (fileImage.isFile())
				fileImage.delete();

		}
	}

	public static boolean cutImage(File sourcePath, int cutNumber, String savePath) {
		try {
			System.out.println("cutNumber :" + cutNumber);
			BufferedImage source = ImageIO.read(sourcePath);
			int allWidth = source.getWidth();
			int allHeight = source.getHeight();
			int width = (int) (allWidth * 1.0 / cutNumber);
			int height = (int) (allHeight * 1.0 / cutNumber);
			System.out.println("cutNumber " + cutNumber);
			for (int i = 0; i < cutNumber; i++)
				for (int j = 0; j < cutNumber; j++) {
					ImageIO.write(source.getSubimage(j * width, i * height, width, height), "jpg",
							new File(savePath + "\\" + (i * cutNumber + j) + ".jpg"));
				}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
