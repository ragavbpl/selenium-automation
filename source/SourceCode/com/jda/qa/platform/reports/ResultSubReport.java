package SourceCode.com.jda.qa.platform.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResultSubReport {

	BufferedWriter writer;

	public ResultSubReport(String resFileLoc) {

		try {

			File resFile = new File(resFileLoc);

			if (!resFile.exists()) {
				resFile.createNewFile();
			} else {
				resFile.delete();
				resFile.createNewFile();
			}

			this.writer = new BufferedWriter(new FileWriter(resFile));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to create the file " + resFileLoc);
		}

	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void closeWriter() {
		try {

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void addTitle(String title) {

		try {
			writer.newLine();
			writer.write("<title>" + title + "</title>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
	
	public void addHeader(String Header) {

		try {
			writer.newLine();
			writer.write("<header align='center'>" + Header + "</header>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}


	public void addHead() {

		try {
			writer.newLine();
			writer.write("<head>");
			writer.newLine();
			writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void endHead() {

		try {
			writer.newLine();
			writer.write("</head>");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void startHtml() {

		try {
			writer.newLine();
			writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
			writer.newLine();
			writer.write("<html>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void endHtml() {

		try {
			writer.newLine();
			writer.write("</html>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void startBody(String bgColor, String fontFamily, String fontSize) {

		try {
			writer.newLine();
			writer.write("<body bgcolor=\"#" + bgColor
					+ "\" style=\"font-family: " + fontFamily + "; font-size: "
					+ fontSize + ";\">");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void endBody() {

		try {
			writer.newLine();
			writer.write("<br>");
			writer.write("<br>");
			writer.write("<br>");
			writer.write("</body>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void addBreak() {

		try {
			writer.newLine();
			writer.write("<br>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void startCenter() {

		try {
			writer.newLine();
			writer.write("<center>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void endCenter() {

		try {
			writer.newLine();
			writer.write("</center>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void insertInBold(String content, String fontSize) {

		try {
			writer.newLine();
			writer.write("<b style=\"font-size: " + fontSize + ";\">" + content
					+ "</b>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void startTable(String align, String width) {

		try {
			writer.newLine();
			writer.write("<table align=\"" + align + "\" width=\"" + width
					+ "\">");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void endTable() {

		try {
			writer.newLine();
			writer.write("</table>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void startTableHeader(String align) {

		try {
			writer.newLine();
			writer.write("<thead align=\"" + align + "\">");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void endTableHeader() {

		try {
			writer.newLine();
			writer.write("</thead>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void startTableBody(String align) {

		try {
			writer.newLine();
			writer.write("<tbody align=\"" + align + "\">");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void endTableBody() {

		try {
			writer.newLine();
			writer.write("</tbody>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void startTableRow(String bgColor) {

		try {
			writer.newLine();
			writer.write("<tr bgcolor=\"#" + bgColor + "\">");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void endTableRow() {

		try {
			writer.newLine();
			writer.write("</tr>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void addTableHeaderColumn(String content) {

		try {
			writer.newLine();
			writer.write("<th>" + content + "</th>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void addTableColumn(String content) {

		try {
			writer.newLine();
			writer.write("<td>" + content + "</td>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
	public void addTableColumn(String content, boolean addTitle) {

		String tagContent = content;
		String title = content;

		try {
			writer.newLine();

			if (addTitle) {

				if (content.length() > 50) {
					String subContent = content.substring(0, 47) + "...";
					tagContent = subContent;
					title = content;
				}
			}
			writer.write("<td align=\"left\"><span title=\"" + title + "\">" + tagContent
					+ "</span></td>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void addTableColumn(String content,String allign) {

		try {
			writer.newLine();
			writer.write("<td align=\""+allign+"\">" + content + "</td>");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void addUnformatedContent(String content) {

		try {
			writer.newLine();
			writer.write(content);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
