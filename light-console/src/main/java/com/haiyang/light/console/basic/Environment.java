package com.haiyang.light.console.basic;

public class Environment {
	private String envName;
	private String gitRepoURL;
	private String gitLocalDir;
	private String gitUserName;
	private String gitPassword;

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public String getGitRepoURL() {
		return gitRepoURL;
	}

	public void setGitRepoURL(String gitRepoURL) {
		this.gitRepoURL = gitRepoURL;
	}

	public String getGitLocalDir() {
		return gitLocalDir;
	}

	public void setGitLocalDir(String gitLocalDir) {
		this.gitLocalDir = gitLocalDir;
	}

	public String getGitUserName() {
		return gitUserName;
	}

	public void setGitUserName(String gitUserName) {
		this.gitUserName = gitUserName;
	}

	public String getGitPassword() {
		return gitPassword;
	}

	public void setGitPassword(String gitPassword) {
		this.gitPassword = gitPassword;
	}

}
