package ro.licenta.pacienti.backend.controller;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Consultation;
import ro.licenta.commons.repository.ConsultationRepository;

@RestController
@RequestMapping("/consultations")
public class ConsultationsController extends DefaultController {

	@Autowired
	private ConsultationRepository consultationRepository;
		
	@GetMapping
	public Mono<List<Consultation>> findByUser() {
		return super.getCurrentUser()
			.flatMapMany(apiToken -> consultationRepository.findByAppointmentUser(apiToken.getUser()))
			.collectList();
	}
	
	@GetMapping("/{id}")
	public Mono<Void> download(@PathVariable("id") ObjectId id, ServerHttpResponse response) {
		return consultationRepository.findById(id).flatMap(consultation -> {
			ReactiveHttpOutputMessage zeroCopyResponse = (ReactiveHttpOutputMessage) response;
			response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
			response.getHeaders().set("filename", "consultation.pdf");
			response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=consultation.pdf");
			return zeroCopyResponse.writeWith(this.generateConsultationPdf(consultation)
				.map(outputStream -> zeroCopyResponse.bufferFactory().wrap(outputStream.toByteArray()))
			).onErrorResume(e -> Mono.empty());
		}).then();
	}
	
	private Mono<ByteArrayOutputStream> generateConsultationPdf(Consultation consultation) {
		return Mono.fromSupplier(() -> {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
		        pdfDoc.setDefaultPageSize(PageSize.A4);
		        Document document = new Document(pdfDoc);
		        document.setMargins(10, 10, 10, 10);
		        document.setHorizontalAlignment(HorizontalAlignment.CENTER);
		        
		        SolidLine line = new SolidLine();
		        line.setColor(ColorConstants.GRAY);
		        LineSeparator separator = new LineSeparator(line);
		        
		        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
		        PdfFont labelFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
		        DeviceRgb labelColor = new DeviceRgb(160,159,159);
		        
		        Paragraph title = new Paragraph("Consultation details");
		        title.setFont(font);
		        title.setFontSize(20f);
		        document.add(title);
		        document.add(separator);
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));
		        
		        Paragraph medicTitle = new Paragraph(consultation.getAppointment().getMedicData().getTitle());
		        medicTitle.setFont(labelFont);
		        medicTitle.setFontColor(labelColor);
		        medicTitle.setFontSize(16f);
		        Paragraph medicName = new Paragraph(consultation.getAppointment().getMedicData().getProfileData().fullName());
		        medicName.setFontSize(18f);
		        document.add(medicTitle);
		        document.add(medicName);
		        document.add(separator);
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));
		        
		        Paragraph patientTitle = new Paragraph("Patient");
		        patientTitle.setFont(labelFont);
		        patientTitle.setFontColor(labelColor);
		        patientTitle.setFontSize(16f);
		        Paragraph patientName = new Paragraph(consultation.getAppointment().getUserData().fullName());
		        patientName.setFontSize(18f);
		        document.add(patientTitle);
		        document.add(patientName);
		        document.add(separator);
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));
		        
		        Paragraph investigationTitle = new Paragraph("Investigation");
		        investigationTitle.setFont(labelFont);
		        investigationTitle.setFontColor(labelColor);
		        investigationTitle.setFontSize(16f);
		        Paragraph investigationName = new Paragraph(consultation.getAppointment().getInvestigationData().getName());
		        investigationName.setFontSize(18f);
		        document.add(investigationTitle);
		        document.add(investigationName);
		        document.add(separator);
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));

		        
		        Paragraph diagnosticTitle = new Paragraph("Diagnostic");
		        diagnosticTitle.setFont(labelFont);
		        diagnosticTitle.setFontColor(labelColor);
		        diagnosticTitle.setFontSize(16f);
		        Paragraph diagnosticName = new Paragraph(consultation.getDiagnostic());
		        diagnosticName.setFontSize(18f);
		        document.add(diagnosticTitle);
		        document.add(diagnosticName);
		        document.add(separator);
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));

		        Paragraph prescriptionTitle = new Paragraph("Prescription");
		        document.add(prescriptionTitle);
		        document.add(separator);
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));
		        document.add(new Paragraph(" "));
		        
		        Table table = new Table(2);
				table.setHorizontalAlignment(HorizontalAlignment.CENTER);
				table.setWidth(UnitValue.createPercentValue(100f));
				Cell name = new Cell();
				name.setFontSize(10);
				name.setVerticalAlignment(VerticalAlignment.MIDDLE);
				name.setHorizontalAlignment(HorizontalAlignment.LEFT);
				name.setBackgroundColor(WebColors.getRGBColor("#2D4154"));
				name.setFontColor(WebColors.getRGBColor("#FFFFFF"));
				name.add(new Paragraph("Name"));
				name.setBorder(Border.NO_BORDER);
		        table.addHeaderCell(name);
		        
		        Cell doze = new Cell();
		        doze.setFontSize(10);
		        doze.setVerticalAlignment(VerticalAlignment.MIDDLE);
		        doze.setHorizontalAlignment(HorizontalAlignment.RIGHT);
				doze.setBackgroundColor(WebColors.getRGBColor("#2D4154"));
				doze.setFontColor(WebColors.getRGBColor("#FFFFFF"));
				doze.add(new Paragraph("Doze"));
		        doze.setBorder(Border.NO_BORDER);
		        table.addHeaderCell(doze);
				
		        consultation.getPrescription().getDrugs().forEach(drug -> {
		        	Cell d = new Cell(); // add the name cell
					d.setFontSize(10);
					d.setBorder(Border.NO_BORDER);
					d.add(new Paragraph(drug.getName()));
					d.setHorizontalAlignment(HorizontalAlignment.LEFT);
					d.setVerticalAlignment(VerticalAlignment.MIDDLE);
					table.addCell(d);
					
		        	Cell n = new Cell(); // add the doze cell
					n.setFontSize(10);
					n.setBorder(Border.NO_BORDER);
					n.add(new Paragraph(drug.getDoze()));
					n.setHorizontalAlignment(HorizontalAlignment.RIGHT);
					n.setVerticalAlignment(VerticalAlignment.MIDDLE);
					table.addCell(n);
		        });
		        document.add(table);
		        document.flush();
				document.close();
				return baos;
			} catch(Exception e) {
				return null;
			}
		});
	}
	
}
