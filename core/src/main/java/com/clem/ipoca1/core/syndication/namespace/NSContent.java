package com.clem.ipoca1.core.syndication.namespace;

import com.clem.ipoca1.core.syndication.handler.HandlerState;

import org.xml.sax.Attributes;

public class NSContent extends Namespace {
	public static final String NSTAG = "content";
	public static final String NSURI = "http://purl.org/rss/1.0/modules/content/";
	
	private static final String ENCODED = "encoded";
	
	@Override
	public SyndElement handleElementStart(String localName, HandlerState state,
			Attributes attributes) {
		return new SyndElement(localName, this);
	}

	@Override
	public void handleElementEnd(String localName, HandlerState state) {
		if (ENCODED.equals(localName) && state.getCurrentItem() != null &&
			state.getContentBuf() != null) {
			state.getCurrentItem().setContentEncoded(state.getContentBuf().toString());
		}
	}

}
