package org.icij.extract.json;

import org.icij.extract.document.Document;
import org.icij.extract.report.Report;
import org.icij.extract.extractor.ExtractionStatus;

import java.util.Iterator;
import java.util.Map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonSerializer;

import org.icij.events.Notifiable;

/**
 * Serializes a {@link Report} to JSON.
 *
 * @author Matthew Caruana Galizia <mcaruana@icij.org>
 * @since 1.0.0-beta
 */
public class ReportSerializer extends JsonSerializer<Report> {

	private final Notifiable monitor;
	private final ExtractionStatus match;

	public ReportSerializer(final Notifiable monitor, final ExtractionStatus match) {
		this.monitor = monitor;
		this.match = match;
	}

	@Override
	public void serialize(final Report report, final JsonGenerator jsonGenerator, final SerializerProvider provider) 
		throws IOException {
		final Iterator<Map.Entry<Document, ExtractionStatus>> iterator = report.entrySet().iterator();

		jsonGenerator.writeStartObject();
		while (iterator.hasNext()) {
			Map.Entry<Document, ExtractionStatus> entry = iterator.next();
			ExtractionStatus result = entry.getValue();

			if (null == match || result.equals(match)) {
				jsonGenerator.writeObjectField(entry.getKey().toString(), result.getCode());
			}

			if (null != monitor) {
				monitor.notifyListeners();
			}
		}

		jsonGenerator.writeEndObject();
	}
}
