package iuh.fit.util;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import javax.swing.table.TableModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Ghi dữ liệu bảng ra file Excel (.xlsx) bằng thư viện fastexcel.
 */
public final class XlsxExportUtil {

	private static final String APP_NAME = "Gara O To Lua";
	private static final String APP_VERSION = "1.0";
	private static final double DEFAULT_COLUMN_WIDTH = 18;

	private XlsxExportUtil() {
	}

	/**
	 * Ghi một sheet gồm dòng tiêu đề (in đậm) và các dòng dữ liệu.
	 *
	 * @param file      file đích
	 * @param sheetName tên sheet (tối đa 31 ký tự)
	 * @param headers   tiêu đề cột
	 * @param rows      dữ liệu, mỗi phần tử là một dòng
	 */
	public static void write(File file, String sheetName, String[] headers, List<Object[]> rows) throws IOException {
		try (OutputStream outputStream = new FileOutputStream(file);
				Workbook workbook = new Workbook(outputStream, APP_NAME, APP_VERSION)) {

			Worksheet worksheet = workbook.newWorksheet(sheetName);

			for (int column = 0; column < headers.length; column++) {
				worksheet.value(0, column, headers[column] == null ? "" : headers[column]);
				worksheet.style(0, column).bold().set();
				worksheet.width(column, DEFAULT_COLUMN_WIDTH);
			}

			for (int row = 0; row < rows.size(); row++) {
				Object[] values = rows.get(row);

				for (int column = 0; column < headers.length; column++) {
					Object value = values != null && column < values.length ? values[column] : null;

					if (value == null) {
						worksheet.value(row + 1, column, "");
					} else if (value instanceof Number number) {
						worksheet.value(row + 1, column, number);
					} else {
						worksheet.value(row + 1, column, value.toString());
					}
				}
			}
		}
	}

	/**
	 * Ghi toàn bộ dữ liệu của một bảng (TableModel) ra file Excel.
	 *
	 * @param file      file đích
	 * @param sheetName tên sheet (tối đa 31 ký tự)
	 * @param model     bảng dữ liệu nguồn
	 */
	public static void write(File file, String sheetName, TableModel model) throws IOException {
		String[] headers = new String[model.getColumnCount()];

		for (int column = 0; column < headers.length; column++) {
			headers[column] = model.getColumnName(column);
		}

		List<Object[]> rows = new ArrayList<>();

		for (int row = 0; row < model.getRowCount(); row++) {
			Object[] values = new Object[headers.length];

			for (int column = 0; column < headers.length; column++) {
				values[column] = model.getValueAt(row, column);
			}

			rows.add(values);
		}

		write(file, sheetName, headers, rows);
	}
}
