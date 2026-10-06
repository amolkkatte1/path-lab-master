package pathlabmaster.pojo;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "HeaderFooter")
public class HeaderFooter {

	@Id
	private Long headerFootId;

	private Long labId;
	@Column(name = "header", columnDefinition = "bytea")
	private byte[] header;
	@Column(name = "footer", columnDefinition = "bytea")
	private byte[] footer;

	private Long createdBy;
	private Long updatedBy;
	private String createdAt;
	private String updatedAt;

	public HeaderFooter() {
		super();
	}

	public HeaderFooter(Long labId, byte[] header, byte[] footer) throws IOException {
		this.labId = labId;
		this.header = header;
		this.footer = footer;
	}

	public Long getHeaderFootId() {
		return headerFootId;
	}

	public void setHeaderFootId(Long headerFootId) {
		this.headerFootId = headerFootId;
	}

	public Long getLabId() {
		return labId;
	}

	public void setLabId(Long labId) {
		this.labId = labId;
	}

	public byte[] getHeader() {
		return header;
	}

	public void setHeader(byte[] header) {
		this.header = header;
	}

	public byte[] getFooter() {
		return footer;
	}

	public void setFooter(byte[] footer) {
		this.footer = footer;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "HeaderFooter [headerFootId=" + headerFootId + ", labId=" + labId + ", header=" + Arrays.toString(header)
				+ ", footer=" + Arrays.toString(footer) + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

}