module toml4j {
	exports com.moandjiezana.toml;
	opens com.moandjiezana.toml to com.google.gson;

	requires com.google.gson;
}