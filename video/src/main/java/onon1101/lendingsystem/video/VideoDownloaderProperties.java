package onon1101.lendingsystem.video;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.video-downloader")
public record VideoDownloaderProperties(boolean enabled, String target) {}
