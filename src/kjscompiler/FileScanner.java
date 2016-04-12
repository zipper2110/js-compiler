/*
 * kjscompiler
 * Copyright (C) 2014  Oleksandr Knyga, oleksandrknyga@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package kjscompiler;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author agnynk
 */
public class FileScanner {
	private static List<String> result;

	public static List<String> run(String rootPath, String search, String projectPath) {
		result = new ArrayList<String>();
		String ptext = "^" + (search.replace(".", "\\.").replace("*", ".*")) + "$";
		
		processFileRecursively(new File(projectPath, rootPath), ptext);
		return result;
	}

	private static void processFileRecursively(File file, String ptext) {
		String absoultePath = file.getAbsolutePath();
		if (file.isFile()) {
			if (Pattern.matches(ptext, absoultePath))
				result.add(absoultePath);
		} else if (file.isDirectory()) {
			File[] files = new File(absoultePath).listFiles();
			for (File f : files) {
				processFileRecursively(f, ptext);
			}
		}
	}

	public static Set<String> resolveDependencies(Collection<String> seedFiles) {
		HashSet<String> fileDependencies = new HashSet<String>();
		for(String seedFile : seedFiles) {
			fileDependencies.addAll(resolveDependencies(seedFile));
		}
		return fileDependencies;
	}

	public static Set<String> resolveDependencies(String fileName) {
		HashSet<String> fileDependencies = new HashSet<String>();

		FileInfo fileInfo = new FileInfo(fileName);

		if(!fileInfo.getIsIgnore() && !fileInfo.getIsExternal()) {
			for(String dependencyFileName : fileInfo.getDependencies()) {
				fileDependencies.addAll(resolveDependencies(dependencyFileName));
			}
			fileDependencies.add(fileName);
		}

		return fileDependencies;
	}
}
