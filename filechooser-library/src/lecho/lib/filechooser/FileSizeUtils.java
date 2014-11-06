package lecho.lib.filechooser;

import java.math.BigDecimal;

public class FileSizeUtils {
	private static final long LENGTH_BYTES_CEIL = 1024;
	private static final long LENGTH_KBYTES_CEIL = 1024 * 1024;
	private static final long LENGTH_MBYTES_CEIL = 1024 * 1024 * 1024;
	// private static final long LENGTH_GBYTES_CEIL = 1024 * 1024 * 1024 * 1024;
	private static final String LENGTH_UNIG_B = "B";
	private static final String LENGTH_UNIT_KB = "KB";
	private static final String LENGTH_UNIT_MB = "MB";
	private static final String LENGTH_UNIT_GB = "GB";

	public static FileSizeDivider getFileSizeDivider(long fileLength) {

		FileSizeDivider divider = new FileSizeDivider();

		if (fileLength < LENGTH_BYTES_CEIL) {
			divider.div = new BigDecimal(1);
			divider.unitText = LENGTH_UNIG_B;

		} else if (fileLength < LENGTH_KBYTES_CEIL) {
			divider.div = new BigDecimal(LENGTH_BYTES_CEIL);
			divider.unitText = LENGTH_UNIT_KB;

		} else if (fileLength < LENGTH_MBYTES_CEIL) {
			divider.div = new BigDecimal(LENGTH_KBYTES_CEIL);
			divider.unitText = LENGTH_UNIT_MB;

		} else {
			divider.div = new BigDecimal(LENGTH_MBYTES_CEIL);
			divider.unitText = LENGTH_UNIT_GB;
		}

		return divider;
	}

	public static class FileSizeDivider {
		public BigDecimal div;
		public String unitText;
	}
}
