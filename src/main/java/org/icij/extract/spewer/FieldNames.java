package org.icij.extract.spewer;

import org.icij.task.Options;
import org.icij.task.annotation.Option;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Defaults for use with spewers.
 *
 * @author Matthew Caruana Galizia <mcaruana@icij.org>
 * @since 1.0.0-beta
 */
@Option(name = "idField", description = "Index field for an automatically generated identifier. The ID " +
		"for the same file is guaranteed not to change if the path doesn't change. Defaults to \"id\".", code = "i",
		parameter = "name")
@Option(name = "textField", description = "Field name for extracted text.", code = "t", parameter = "name")
@Option(name = "pathField", description = "Field name for the file path.", parameter = "name", code = "p")
@Option(name = "parentPathField", description = "Field name for the parent directory path.", parameter = "name")
@Option(name = "baseTypeField", description = "Field name for the base content-type.", parameter = "name")
@Option(name = "versionField", description = "Index field name for the version.", parameter = "name")
@Option(name = "metadataPrefix", description = "Prefix for metadata fields added to the index.", parameter = "name")
public class FieldNames {

	private static final Pattern fieldName = Pattern.compile("[^A-Za-z0-9_]");

	public static final String DEFAULT_ID_FIELD = "extract_id";
	public static final String DEFAULT_TEXT_FIELD = "tika_content";
	public static final String DEFAULT_PATH_FIELD = "extract_paths";
	public static final String DEFAULT_BASE_TYPE_FIELD = "extract_base_type";
	public static final String DEFAULT_PARENT_PATH_FIELD = "extract_parent_paths";
	public static final String DEFAULT_VERSION_FIELD = "_version_";
	public static final String DEFAULT_METADATA_FIELD_PREFIX = "tika_metadata_";

	private String textField = DEFAULT_TEXT_FIELD;
	private String pathField = DEFAULT_PATH_FIELD;
	private String parentPathField = DEFAULT_PARENT_PATH_FIELD;
	private String idField = DEFAULT_ID_FIELD;
	private String baseTypeField = DEFAULT_BASE_TYPE_FIELD;
	private String versionField = DEFAULT_VERSION_FIELD;
	private String metadataFieldPrefix = DEFAULT_METADATA_FIELD_PREFIX;

	public FieldNames configure(final Options<String> options) {
		options.get("textField").value().ifPresent(this::forText);
		options.get("pathField").value().ifPresent(this::forPath);
		options.get("parentPathField").value().ifPresent(this::forParentPath);
		options.get("baseTypeField").value().ifPresent(this::forBaseType);
		options.get("versionField").value().ifPresent(this::forVersion);
		options.get("idField").value().ifPresent(this::forId);
		options.get("metadataPrefix").value().ifPresent(this::forMetadataPrefix);

		return this;
	}

	private void forText(final String textField) {
		this.textField = textField;
	}

	String forText() {
		return textField;
	}

	private void forPath(final String pathField) {
		this.pathField = pathField;
	}

	String forPath() {
		return pathField;
	}

	private void forParentPath(final String parentPathField) {
		this.parentPathField = parentPathField;
	}

	String forParentPath() {
		return parentPathField;
	}

	private void forBaseType(final String baseTypeField) {
		this.baseTypeField = baseTypeField;
	}

	String forBaseType() {
		return baseTypeField;
	}

	private void forVersion(final String versionField) {
		this.versionField = versionField;
	}

	String forVersion() {
		return versionField;
	}

	private void forId(final String idField) {
		this.idField = idField;
	}

	String forId() {
		return idField;
	}

	private void forMetadataPrefix(final String metadataFieldPrefix) {
		this.metadataFieldPrefix = metadataFieldPrefix;
	}

	String forMetadata(final String name) {
		final String normalizedName = fieldName.matcher(name).replaceAll("_").toLowerCase(Locale.ROOT);

		if (null != metadataFieldPrefix) {
			return metadataFieldPrefix + normalizedName;
		}

		return normalizedName;
	}

	String forTagPrefix() {
		return "tag_";
	}
}
