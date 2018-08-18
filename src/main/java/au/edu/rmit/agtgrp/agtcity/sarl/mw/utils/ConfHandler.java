/** 
 * SARL-MASSIM - Interface between the SARL agent-oriented language 
 * and the MASSIM 2017 server
 * Copyright (C) 2017 The SARL-MASSIM Authors.
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

package au.edu.rmit.agtgrp.agtcity.sarl.mw.utils;

import java.io.File;
import java.util.Scanner;

/**
 * Extracted from javaagents of the MASSim 2017 project.
 * credit to the original authors of MASSim 2017.
 *
 */
public class ConfHandler {

    /**
     * A method of finding a "conf" directory inside the current working directory. s
     * @return String The config Directory path
     */
    public static String getConfDir() {
        String configDir = "";
        File confDir = new File("conf");
        confDir.mkdirs();
        File[] confFiles = confDir.listFiles(File::isDirectory);

        if (confFiles == null || confFiles.length == 0) {
            System.out.println("No Config files found");
            System.exit(0);
        } else {
            System.out.println("Choose a number:");
            for (int i = 0; i < confFiles.length; i++) {
                System.out.println(i + " " + confFiles[i]);
            }
            Scanner in = new Scanner(System.in);
            Integer confNum = null;
            while (confNum == null) {
                try {
                    confNum = Integer.parseInt(in.next());
                    if (confNum < 0 || confNum > confFiles.length - 1) {
                        System.out.println("No config for that number, try again:");
                        confNum = null;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid number, try again:");
                }
            }
            in.close();
            configDir = confFiles[confNum].getPath();
        }
        return configDir;
    }
}