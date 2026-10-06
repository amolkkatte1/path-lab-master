package pathlabmaster.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.PageSize;

import pathlabmaster.dao.ClientConfigRepository;
import pathlabmaster.dao.HeaderFooterRepository;
import pathlabmaster.pojo.ClientConfig;
import pathlabmaster.pojo.HeaderFooter;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class HeaderFooterService implements IHeaderFooterService{

	@Autowired
	private HeaderFooterRepository hfRepo;
	@Autowired
	private ClientConfigRepository clientConfigRepo;
	
	@Override
	public Response createHeaderFooter(Long labId, MultipartFile header, MultipartFile footer) throws IOException {
		HeaderFooter headerFooterExisting = hfRepo.findByLabId(labId);
		ClientConfig clientConfig = clientConfigRepo.findByLabId(labId);
		float maxWidth = PageSize.A4.getWidth();
		float maxHeight = 78 + clientConfig.getReportTopSpace();
		float maxHeightFooter = clientConfig.getReportBottomSpace();
		HeaderFooter headerFooter = new HeaderFooter(labId,resizeImage(header,maxWidth,maxHeight),resizeImage(footer, maxWidth, maxHeightFooter));
		HeaderFooter saved = null;
		if (headerFooterExisting == null) {
			headerFooter.setHeaderFootId(Utility.generateId());
			headerFooter.setUpdatedAt(Utility.getCurrentTime());
			headerFooter.setCreatedAt(Utility.getCurrentTime());
			saved = hfRepo.save(headerFooter);
		} else {
		    // Record already exists → update existing
			headerFooter.setUpdatedAt(Utility.getCurrentTime());
			headerFooter.setHeaderFootId(headerFooterExisting.getHeaderFootId());
			saved = hfRepo.save(headerFooter);
		}
		
		return new Response(ResponseStatus.success, 1, "createHeaderFooter created/updated successfully", saved);
	}

	private byte[] resizeImage(MultipartFile file, float maxWidth, float maxHeight) throws IOException {

		BufferedImage original = ImageIO.read(file.getInputStream());

		if (original == null) {
			throw new IOException("Invalid image file");
		}

		double widthRatio = maxWidth / original.getWidth();
		double heightRatio = maxHeight / original.getHeight();

		double ratio = Math.min(widthRatio, heightRatio);

		int newWidth = (int) (original.getWidth() * ratio);
		int newHeight = (int) (original.getHeight() * ratio);

		BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = resized.createGraphics();

		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2d.drawImage(original, 0, 0, newWidth, newHeight, null);

		g2d.dispose();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(resized, "jpg", outputStream);

		return outputStream.toByteArray();
	}
}
