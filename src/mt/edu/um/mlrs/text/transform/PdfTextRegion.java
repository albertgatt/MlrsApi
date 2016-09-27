package mt.edu.um.mlrs.text.transform;

import java.awt.Rectangle;

import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.common.PDRectangle;

public enum PdfTextRegion {

	LEFT, RIGHT, ALL;

	public float[] getCoordinates(PDPage page) {
		PDRectangle origBox = page.findCropBox();
		float width = origBox.getWidth();
		float[] coords;

		switch (this) {
		case LEFT:
			coords = new float[] { origBox.getLowerLeftX(),
					origBox.getLowerLeftY(), origBox.getUpperRightX() / 2,
					origBox.getUpperRightY() };
			break;

		case RIGHT:
			coords = new float[] { origBox.getLowerLeftX() + width / 2,
					origBox.getLowerLeftY(), origBox.getUpperRightX(),
					origBox.getUpperRightY() };
			break;

		default:
			coords = new float[] { origBox.getLowerLeftX(),
					origBox.getLowerLeftY(), origBox.getUpperRightX(),
					origBox.getUpperRightY() };
			break;
		}

		return coords;

	}

	public Rectangle getRectangle(PDPage page) {
		float[] coords = getCoordinates(page);

		return new Rectangle(Math.round(coords[0]), Math.round(coords[1]), Math
				.round(coords[2]), Math.round(coords[3]));
	}

}
