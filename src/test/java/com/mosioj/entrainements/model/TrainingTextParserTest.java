package com.mosioj.entrainements.model;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TrainingTextParserTest {

	private TrainingTextParser readTraining(String filePathInTraining) throws IOException {
		String text = FileUtils.readFileToString(	new File(this.getClass().getResource("/trainings/" + filePathInTraining).getFile()),
													StandardCharsets.UTF_8);
		return new TrainingTextParser(text);
	}

	@Test
	public void test_06092018() throws IOException {

		TrainingTextParser parser = readTraining("jeudi_06_09_2018.txt");

		assertEquals(3300, parser.getTrainingSize());
	}

	@Test
	public void test_2309() throws IOException {
		
		TrainingTextParser parser = readTraining("mardi_23_09.txt");
		
		assertEquals(3200, parser.getTrainingSize());
	}

	@Test
	public void test_2609() throws IOException {
		
		TrainingTextParser parser = readTraining("vendredi_2609.txt");
		
		assertEquals(2600, parser.getTrainingSize());
	}

	@Test
	public void test_27092018() throws IOException {
		
		TrainingTextParser parser = readTraining("jeudi_27_09_2018.txt");
		
		assertEquals(3500, parser.getTrainingSize());
	}

	@Test
	public void test_2909() throws IOException {
		
		TrainingTextParser parser = readTraining("lundi_29_09.txt");
		
		assertEquals(3200, parser.getTrainingSize());
	}
	
	@Test
	public void test_04_10_2018() throws IOException {
		
		TrainingTextParser parser = readTraining("jeudi_04_10_2018.txt");
		
		assertEquals(4000, parser.getTrainingSize());
	}

	@Test
	public void test_17_10_2014() throws IOException {
		
		TrainingTextParser parser = readTraining("vendredi_17_10_2014.txt");
		
		assertEquals(2800, parser.getTrainingSize());
	}
	
	@Test
	public void test_18_10_2018() throws IOException {
		
		TrainingTextParser parser = readTraining("jeudi_18_10_2018.txt");
		
		assertEquals(3800, parser.getTrainingSize());
	}
	
	@Test
	public void test_20_10_2018() throws IOException {
		
		TrainingTextParser parser = readTraining("samedi_20_10_2018.txt");
		
		assertEquals(5000, parser.getTrainingSize());
	}

	@Test
	public void test_08_12_2014() throws IOException {
		
		TrainingTextParser parser = readTraining("lundi_08_12_2014.txt");
		
		assertEquals(3500, parser.getTrainingSize());
	}

	@Test
	public void test_13_12_2014() throws IOException {
		
		TrainingTextParser parser = readTraining("samedi_13_12_2014.txt");
		
		assertEquals(5100, parser.getTrainingSize());
	}

}
