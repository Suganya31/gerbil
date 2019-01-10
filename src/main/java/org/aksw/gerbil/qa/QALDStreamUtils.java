package org.aksw.gerbil.qa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedJson;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.qa.commons.load.json.QaldJson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class QALDStreamUtils {

	public static final String MISSING_ADAPTER_NAME = "missingAdapterName";

	/**
	 * This method parses QALD questions from the given input stream using the
	 * given {@link QALDStreamType}.
	 * 
	 * @param in
	 *            the input stream from which the questions are read
	 * @param streamType
	 *            the type of the stream
	 * @return a list of {@link Document} instances containing the questions
	 *         that could be parsed.
	 * @deprecated The usage of this method is not suggested since it uses the
	 *             static {@link #MISSING_ADAPTER_NAME} attribute as adapter
	 *             name. Use
	 *             {@link #parseDocument(InputStream, QALDStreamType, String)}
	 *             with a correct adapter name, instead.
	 */
	@Deprecated
	public static List<Document> parseDocument(InputStream in, QALDStreamType streamType) {
		return parseDocument(in, streamType, MISSING_ADAPTER_NAME);
	}

	/**
	 * This method parses QALD questions from the given input stream using the
	 * given {@link QALDStreamType}. The given adapter name is needed to
	 * generate URIs for the questions.
	 * 
	 * @param in
	 *            the input stream from which the questions are read
	 * @param streamType
	 *            the type of the stream
	 * @param adapterName
	 *            the name of the adapter for which the parsing is done
	 * @return a list of {@link Document} instances containing the questions
	 *         that could be parsed.
	 */
	public static List<Document> parseDocument(InputStream in, QALDStreamType streamType, String adapterName) {
		List<IQuestion> questions;
		try {
			switch (streamType) {
			case JSON:
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int n = 0;
				while ((n = in.read(buf)) >= 0)
				    baos.write(buf, 0, n);
				byte[] content = baos.toByteArray();
				//test if extended json
				try {
					if (null == (questions = EJQuestionFactory.getQuestionsFromExtendedJson((ExtendedJson) ExtendedQALDJSONLoader.readJson(new ByteArrayInputStream(content), ExtendedJson.class)))) {
						throw new IllegalArgumentException("Could not load JSON stream");
					}
				}catch(JsonParseException | JsonMappingException e) {
					//test if qald
					if (null == (questions = EJQuestionFactory.getQuestionsFromJson((QaldJson) ExtendedQALDJSONLoader.readJson(new ByteArrayInputStream(content), QaldJson.class)))) {
						throw new IllegalArgumentException("Could not load JSON stream");
					}
				}
				break;
			case XML:
				questions = LoaderController.loadXML(in, "en");
				break;
			default:
				throw new IllegalArgumentException("Got an unknown QALD stream type " + streamType);
			}
		} catch (IOException e1) {
			throw new IllegalArgumentException("Got an unknown QALD stream type " + streamType);
		}

		String questionUriPrefix;
		try {
			questionUriPrefix = "http://qa.gerbil.aksw.org/" + URLEncoder.encode(adapterName, "UTF-8") + "/question#";
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Severe error while trying to encode adapter name.", e);
		}

		List<Document> instances = new ArrayList<Document>(questions.size());
		for (IQuestion question : questions) {
			instances.add(QAUtils.translateQuestion(question, questionUriPrefix + question.getId(), "en"));
		}
		return instances;
	}
}
