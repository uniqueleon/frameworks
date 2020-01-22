package org.aztec.framework.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitUtils {

	public static Git clone(String url, String username, String password, String dirPath)
			throws InvalidRemoteException, TransportException, GitAPIException {
		UsernamePasswordCredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			Git git = Git.cloneRepository().setURI(url) // 设置远程URI
					.setBranch("master") // 设置clone下来的分支,默认master
					.setDirectory(new File(dirPath)) // 设置下载存放路径
					.call();
			return git;
		} else {
			Git git = Git.cloneRepository().setURI(url) // 设置远程URI
					.setBranch("master") // 设置clone下来的分支,默认master
					.setDirectory(new File(dirPath)) // 设置下载存放路径
					.setCredentialsProvider(provider) // 设置权限验证
					.call();

			return git;
		}
	}

	public static Git getGitInstance(String dirPath) throws IOException, GitAPIException {
		File gitDir = new File(dirPath);
		if (gitDir.exists()) {
			Repository repository = new FileRepository(gitDir);
			Git git = new Git(repository);

			return git;
		} else {
			throw new FileNotFoundException("No git directory[" + dirPath + "] found!");
		}
	}

	public static PullResult pull(Git git, String username, String password, String branch, Writer console)
			throws WrongRepositoryStateException, InvalidConfigurationException, InvalidRemoteException,
			CanceledException, RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException,
			GitAPIException {
		UsernamePasswordCredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
		PullResult pr = git.pull().setProgressMonitor(new TextProgressMonitor(console)).setRemoteBranchName(branch)
				.setCredentialsProvider(provider)
				// .setRemote("origin")
				// .setRebase(false)
				.call();
		return pr;
	}
	
	public static String getlastCommitHashcode(Git git, String username, String password, String branch, Writer console) throws WrongRepositoryStateException, InvalidConfigurationException, InvalidRemoteException, CanceledException, RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException, GitAPIException {
		PullResult pr = pull(git, username, password, branch, console);
		ObjectId head = pr.getMergeResult().getNewHead();
		return head.getName();
	}

	public static void commit(Git git,List<String> filePaths,String comment)
			throws NoWorkTreeException, NoFilepatternException, GitAPIException {

		//String filePattern = git.getRepository().getWorkTree().getPath();
		AddCommand addCmd = git.add();
		for(String filePath : filePaths) {
			addCmd.addFilepattern(filePath);
			//addCmd.addFilepattern(filePattern + "/" + filePath);
		}
		addCmd.call();
		git.commit().setAll(true).setMessage(comment).call();
		// git.push().setCredentialsProvider(provider).setProgressMonitor(new
		// TextProgressMonitor(console)).call();

	}
	
	public static void push(Git git, String username, String password,Writer console)
			throws NoWorkTreeException, NoFilepatternException, GitAPIException {
		UsernamePasswordCredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
		git.push().setCredentialsProvider(provider).setProgressMonitor(new TextProgressMonitor(console)).call();
	}
	

	public static void main(String[] args) {
		try {
			// Git git = clone("https://github.com/uniqueleon/snakeeater.git", "uniqueleon",
			// "g13422345952h", "D://data/git/snakeeater");
			
			String username = "uniqueleon";
			String password = "g13422345952h",branch = "master";
			PrintWriter console = new PrintWriter(System.out); Git git =
			getGitInstance("D://data/git/snakeeater/.git");
			/*pull(git, username,
			password,"master",pr);*/
			String hashCode = getlastCommitHashcode(git, username, password, branch, console);
			System.out.println(hashCode);
			//commit(git, Lists.newArrayList("readme1.txt"),"commit");
			//push(git, username, password,pr);
			//pull(git, username, password, branch, pr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
