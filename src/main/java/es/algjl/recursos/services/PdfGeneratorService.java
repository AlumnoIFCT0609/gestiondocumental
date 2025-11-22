package es.algjl.recursos.services;


import es.algjl.recursos.models.RecursoElaborado;
import es.algjl.recursos.models.Recurso;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGeneratorService {

    @Value("${app.recursos.directorio-generados}")
    private String directorioGenerados;

    public byte[] generarPDFDesdeRecurso(RecursoElaborado recurso) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Título
            Paragraph titulo = new Paragraph(recurso.getNombre())
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Información del recurso
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Creado por: " + recurso.getUsuarioCreador().getNombre()));
            document.add(new Paragraph("Fecha: " + 
                    recurso.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph("Uso: " + recurso.getUso()));
            document.add(new Paragraph("Materia: " + recurso.getMateria()));
            
            if (recurso.getLicencia() != null) {
                document.add(new Paragraph("Licencia: " + recurso.getLicencia().getLicencia()));
            }

            document.add(new Paragraph("\n---\n"));

            // Recursos utilizados
            if (!recurso.getRecursosUsados().isEmpty()) {
                document.add(new Paragraph("Recursos utilizados:").setBold());
                for (Recurso rec : recurso.getRecursosUsados()) {
                    document.add(new Paragraph("• " + rec.getNombre() + 
                            " (" + rec.getTipoRecurso() + ")"));
                }
            }

            document.add(new Paragraph("\n---\n"));

            // Contenido (simplificado - aquí puedes mejorar con el HTML parseado)
            if (recurso.getDatosComposicion() != null) {
                document.add(new Paragraph("Composición:").setBold());
                // Aquí podrías parsear el HTML y convertirlo a elementos de iText
                document.add(new Paragraph(stripHtml(recurso.getDatosComposicion())));
            }

            // Pie de página
            document.add(new Paragraph("\n\n---\n")
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Generado con Gestión Documental")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();

            // Guardar también en disco
            guardarPDFEnDisco(recurso.getNombre(), baos.toByteArray());

            log.info("PDF generado correctamente para: {}", recurso.getNombre());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    private void guardarPDFEnDisco(String nombre, byte[] contenido) {
        try {
            File directorio = new File(directorioGenerados);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            String nombreArchivo = nombre.replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf";
            File archivo = new File(directorio, nombreArchivo);

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                fos.write(contenido);
            }

            log.info("PDF guardado en disco: {}", archivo.getAbsolutePath());
        } catch (Exception e) {
            log.error("Error al guardar PDF en disco: {}", e.getMessage(), e);
        }
    }

    private String stripHtml(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]*>", "").trim();
    }
}