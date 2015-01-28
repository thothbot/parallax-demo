/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file is part of Parallax project.
 * 
 * Parallax is free software: you can redistribute it and/or modify it 
 * under the terms of the Creative Commons Attribution 3.0 Unported License.
 * 
 * Parallax is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the Creative Commons Attribution 
 * 3.0 Unported License. for more details.
 * 
 * You should have received a copy of the the Creative Commons Attribution 
 * 3.0 Unported License along with Parallax. 
 * If not, see http://creativecommons.org/licenses/by/3.0/.
 */

package thothbot.parallax.demo.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.DataModel;
import thothbot.parallax.demo.resources.DemoResources;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.thirdparty.guava.common.base.Joiner;

public class DemoGenerator extends Generator 
{
	/**
	 * The class loader used to get resources.
	 */
	private ClassLoader classLoader = null;
	
	/**
	 * The generator context.
	 */
	private GeneratorContext context = null;
	
	/**
	 * The {@link TreeLogger} used to log messages.
	 */
	private TreeLogger logger = null;
	
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException 
	{

		this.logger = logger;
		this.context = context;
		this.classLoader = Thread.currentThread().getContextClassLoader();
		
		// Only generate files on the first permutation
		if (!isFirstPass())
			return null;

//		final SingleSelectionModel<ContentWidget> selectionModel = new SingleSelectionModel<ContentWidget>();
//		final DataModel treeModel = new DataModel(selectionModel);

//		generateSourceFiles(treeModel);
		
		return null;
	}
	
	private void generateSourceFiles(DataModel treeModel) throws UnableToCompleteException
	{
		// Get each data code block
		List<String> jsons = new ArrayList<String>(); 

		// Get the file contents
		// Generate the source and raw source files
		for (ContentWidget widget : treeModel. getAllContentWidgets()) 
		{
			String json = "{\n";
			json += "\t\"description\": \"" + widget.getName() + "\"\n";
			json += "\t\"icon\": \""  + widget.getIcon() + "\"\n";
			json += "\t\"url\":  \"#" + widget.getClass().getSimpleName() + "\"\n";
			json += "}";
			jsons.add(json);
		}
		
		String retval = "[" + Joiner.on(",\n").join(jsons) + "]";	
		createPublicResource(retval);
	}
	
	private String getResourceContents(String path) throws UnableToCompleteException
	{
		InputStream in = classLoader.getResourceAsStream(path);
		if (in == null) 
		{
			logger.log(TreeLogger.ERROR, "Resource not found: " + path);
			throw new UnableToCompleteException();
		}

		StringBuffer fileContentsBuf = new StringBuffer();
		BufferedReader br = null;
		try 
		{
			br = new BufferedReader(new InputStreamReader(in));
			String temp;
			while ((temp = br.readLine()) != null)
				fileContentsBuf.append(temp).append('\n');

		} 
		catch (IOException e) 
		{
			logger.log(TreeLogger.ERROR, "Cannot read resource", e);
			throw new UnableToCompleteException();
		} 
		finally 
		{
			if (br != null) 
			{
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}

		// Return the file contents as a string
		return fileContentsBuf.toString();
	}

	/**
	 * Set the full contents of a resource in the public directory.
	 * 
	 * @param partialPath
	 *            the path to the file relative to the public directory
	 * @param contents
	 *            the file contents
	 */
	private void createPublicResource(String contents)
			throws UnableToCompleteException
	{
		String partialPath = DemoResources.DST_DEMO_JSON;

		try 
		{
			OutputStream outStream = context.tryCreateResource(logger, partialPath);

			if (outStream == null) 
			{
				String message = "Attempting to generate duplicate public resource: " + partialPath;
				logger.log(TreeLogger.ERROR, message);
				throw new UnableToCompleteException();
			}

			outStream.write(contents.getBytes());
			context.commitResource(logger, outStream);
		} 
		catch (IOException e) 
		{
			logger.log(TreeLogger.ERROR, "Error writing file: " + partialPath, e);
			throw new UnableToCompleteException();
		}
	}
	
	/**
	 * Ensure that we only generate files once by creating a placeholder file,
	 * then looking for it on subsequent generates.
	 * 
	 * @return true if this is the first pass, false if not
	 */
	private boolean isFirstPass()
	{
		String placeholder = DemoResources.DST_DEMO_JSON + ".generated";
		try
		{
			OutputStream outStream = context.tryCreateResource(logger, placeholder);
			if (outStream == null)
				return false;
			else
				context.commitResource(logger, outStream);

		} 
		catch (UnableToCompleteException e) 
		{
			logger.log(TreeLogger.ERROR, "Unable to generate", e);
			return false;
		}
		return true;
	}

}
