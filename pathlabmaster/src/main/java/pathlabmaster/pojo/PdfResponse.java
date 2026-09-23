package pathlabmaster.pojo;

public class PdfResponse {

    private byte[] pdf;
    private String fileName;

    public PdfResponse(byte[] pdf, String fileName) {
        this.pdf = pdf;
        this.fileName = fileName;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public String getFileName() {
        return fileName;
    }
}