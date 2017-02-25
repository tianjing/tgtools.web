package tgtools.web.rests.entity;

import java.util.Map;

public class SettingsEntity {
	private Map<String,String> configs;

	public Map<String, String> getConfigs() {
		return configs;
	}

	public void setConfigs(Map<String, String> setting) {
		this.configs = setting;
	}
}
