package com.clem.ipoca.core.syndication.namespace.atom;

import android.text.TextUtils;
import android.util.Log;

import com.clem.ipoca.core.feed.FeedImage;
import com.clem.ipoca.core.feed.FeedItem;
import com.clem.ipoca.core.feed.FeedMedia;
import com.clem.ipoca.core.syndication.handler.HandlerState;
import com.clem.ipoca.core.syndication.namespace.NSITunes;
import com.clem.ipoca.core.syndication.namespace.NSRSS20;
import com.clem.ipoca.core.syndication.namespace.Namespace;
import com.clem.ipoca.core.syndication.namespace.SyndElement;
import com.clem.ipoca.core.syndication.util.SyndTypeUtils;
import com.clem.ipoca.core.util.DateUtils;

import org.xml.sax.Attributes;

public class NSAtom extends Namespace {
    private static final String TAG = "NSAtom";
    public static final String NSTAG = "atom";
    public static final String NSURI = "http://www.w3.org/2005/Atom";

    private static final String FEED = "feed";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String ENTRY = "entry";
    private static final String LINK = "link";
    private static final String UPDATED = "updated";
    private static final String AUTHOR = "author";
    private static final String AUTHOR_NAME = "name";
    private static final String CONTENT = "content";
    private static final String SUMMARY = "summary";
    private static final String IMAGE_LOGO = "logo";
    private static final String IMAGE_ICON = "icon";
    private static final String SUBTITLE = "subtitle";
    private static final String PUBLISHED = "published";

    private static final String TEXT_TYPE = "type";
    // Link
    private static final String LINK_HREF = "href";
    private static final String LINK_REL = "rel";
    private static final String LINK_TYPE = "type";
    private static final String LINK_TITLE = "title";
    private static final String LINK_LENGTH = "length";
    // rel-values
    private static final String LINK_REL_ALTERNATE = "alternate";
    private static final String LINK_REL_ARCHIVES = "archives";
    private static final String LINK_REL_ENCLOSURE = "enclosure";
    private static final String LINK_REL_PAYMENT = "payment";
    private static final String LINK_REL_RELATED = "related";
    private static final String LINK_REL_SELF = "self";
    private static final String LINK_REL_NEXT = "next";
    // type-values
    private static final String LINK_TYPE_ATOM = "application/atom+xml";
    private static final String LINK_TYPE_HTML = "text/html";
    private static final String LINK_TYPE_XHTML = "application/xml+xhtml";

    private static final String LINK_TYPE_RSS = "application/rss+xml";

    /**
     * Regexp to test whether an Element is a Text Element.
     */
    private static final String isText = TITLE + "|" + CONTENT + "|"
            + SUBTITLE + "|" + SUMMARY;

    public static final String isFeed = FEED + "|" + NSRSS20.CHANNEL;
    public static final String isFeedItem = ENTRY + "|" + NSRSS20.ITEM;

    @Override
    public SyndElement handleElementStart(String localName, HandlerState state,
                                          Attributes attributes) {
        if (ENTRY.equals(localName)) {
            state.setCurrentItem(new FeedItem());
            state.getItems().add(state.getCurrentItem());
            state.getCurrentItem().setFeed(state.getFeed());
        } else if (localName.matches(isText)) {
            String type = attributes.getValue(TEXT_TYPE);
            return new AtomText(localName, this, type);
        } else if (LINK.equals(localName)) {
            String href = attributes.getValue(LINK_HREF);
            String rel = attributes.getValue(LINK_REL);
            SyndElement parent = state.getTagstack().peek();
            if (parent.getName().matches(isFeedItem)) {
                if (LINK_REL_ALTERNATE.equals(rel)) {
                    state.getCurrentItem().setLink(href);
                } else if (LINK_REL_ENCLOSURE.equals(rel)) {
                    String strSize = attributes.getValue(LINK_LENGTH);
                    long size = 0;
                    try {
                        if (strSize != null) {
                            size = Long.parseLong(strSize);
                        }
                    } catch (NumberFormatException e) {
                        Log.d(TAG, "Length attribute could not be parsed.");
                    }
                    String type = attributes.getValue(LINK_TYPE);

                    if (type == null) {
                        type = SyndTypeUtils.getMimeTypeFromUrl(href);
                    }

                    if(SyndTypeUtils.enclosureTypeValid(type)) {
                        FeedItem currItem = state.getCurrentItem();
                        if(currItem != null && !currItem.hasMedia()) {
                            currItem.setMedia(new FeedMedia(currItem, href, size, type));
                        }
                    }
                } else if (LINK_REL_PAYMENT.equals(rel)) {
                    state.getCurrentItem().setPaymentLink(href);
                }
            } else if (parent.getName().matches(isFeed)) {
                if (LINK_REL_ALTERNATE.equals(rel)) {
                    String type = attributes.getValue(LINK_TYPE);
                    /*
                     * Use as link if a) no type-attribute is given and
					 * feed-object has no link yet b) type of link is
					 * LINK_TYPE_HTML or LINK_TYPE_XHTML
					 */
                    if (state.getFeed() != null &&
                        ((type == null && state.getFeed().getLink() == null) ||
                            (LINK_TYPE_HTML.equals(type) || LINK_TYPE_XHTML.equals(type)))) {
                        state.getFeed().setLink(href);
                    } else if (LINK_TYPE_ATOM.equals(type) || LINK_TYPE_RSS.equals(type)) {
                        // treat as podlove alternate feed
                        String title = attributes.getValue(LINK_TITLE);
                        if (TextUtils.isEmpty(title)) {
                            title = href;
                        }
                        state.addAlternateFeedUrl(title, href);
                    }
                } else if (LINK_REL_ARCHIVES.equals(rel) && state.getFeed() != null) {
                    String type = attributes.getValue(LINK_TYPE);
                    if (LINK_TYPE_ATOM.equals(type) || LINK_TYPE_RSS.equals(type)) {
                        String title = attributes.getValue(LINK_TITLE);
                        if (TextUtils.isEmpty(title)) {
                            title = href;
                        }
                        state.addAlternateFeedUrl(title, href);
                    } else if (LINK_TYPE_HTML.equals(type) || LINK_TYPE_XHTML.equals(type)) {
                        //A Link such as to a directory such as iTunes
                    }
                } else if (LINK_REL_PAYMENT.equals(rel) && state.getFeed() != null) {
                    state.getFeed().setPaymentLink(href);
                } else if (LINK_REL_NEXT.equals(rel) && state.getFeed() != null) {
                    state.getFeed().setPaged(true);
                    state.getFeed().setNextPageLink(href);
                }
            }
        }
        return new SyndElement(localName, this);
    }

    @Override
    public void handleElementEnd(String localName, HandlerState state) {
        if (ENTRY.equals(localName)) {
            if (state.getCurrentItem() != null &&
                    state.getTempObjects().containsKey(NSITunes.DURATION)) {
                FeedItem currentItem = state.getCurrentItem();
                if (currentItem.hasMedia()) {
                    Integer duration = (Integer) state.getTempObjects().get(NSITunes.DURATION);
                    currentItem.getMedia().setDuration(duration);
                }
                state.getTempObjects().remove(NSITunes.DURATION);
            }
            state.setCurrentItem(null);
        }

        if (state.getTagstack().size() >= 2) {
            AtomText textElement = null;
            String content;
            if (state.getContentBuf() != null) {
                content = state.getContentBuf().toString();
            } else {
                content = "";
            }
            SyndElement topElement = state.getTagstack().peek();
            String top = topElement.getName();
            SyndElement secondElement = state.getSecondTag();
            String second = secondElement.getName();

            if (top.matches(isText)) {
                textElement = (AtomText) topElement;
                textElement.setContent(content);
            }

            if (ID.equals(top)) {
                if (FEED.equals(second) && state.getFeed() != null) {
                    state.getFeed().setFeedIdentifier(content);
                } else if (ENTRY.equals(second) && state.getCurrentItem() != null) {
                    state.getCurrentItem().setItemIdentifier(content);
                }
            } else if (TITLE.equals(top) && textElement != null) {
                if (FEED.equals(second) && state.getFeed() != null) {
                    state.getFeed().setTitle(textElement.getProcessedContent());
                } else if (ENTRY.equals(second) && state.getCurrentItem() != null) {
                    state.getCurrentItem().setTitle(textElement.getProcessedContent());
                }
            } else if (SUBTITLE.equals(top) && FEED.equals(second) && textElement != null &&
                state.getFeed() != null) {
                state.getFeed().setDescription(textElement.getProcessedContent());
            } else if (CONTENT.equals(top) && ENTRY.equals(second) && textElement != null &&
                state.getCurrentItem() != null) {
                state.getCurrentItem().setDescription(textElement.getProcessedContent());
            } else if (SUMMARY.equals(top) && ENTRY.equals(second) && textElement != null &&
                state.getCurrentItem() != null && state.getCurrentItem().getDescription() == null) {
                state.getCurrentItem().setDescription(textElement.getProcessedContent());
            } else if (UPDATED.equals(top) && ENTRY.equals(second) && state.getCurrentItem() != null &&
                state.getCurrentItem().getPubDate() == null) {
                state.getCurrentItem().setPubDate(DateUtils.parse(content));
            } else if (PUBLISHED.equals(top) && ENTRY.equals(second) && state.getCurrentItem() != null) {
                state.getCurrentItem().setPubDate(DateUtils.parse(content));
            } else if (IMAGE_LOGO.equals(top) && state.getFeed() != null && state.getFeed().getImage() == null) {
                state.getFeed().setImage(new FeedImage(state.getFeed(), content, null));
            } else if (IMAGE_ICON.equals(top) && state.getFeed() != null) {
                state.getFeed().setImage(new FeedImage(state.getFeed(), content, null));
            } else if (AUTHOR_NAME.equals(top) && AUTHOR.equals(second) &&
                    state.getFeed() != null && state.getCurrentItem() == null) {
                String currentName = state.getFeed().getAuthor();
                if (currentName == null) {
                    state.getFeed().setAuthor(content);
                } else {
                    state.getFeed().setAuthor(currentName + ", " + content);
                }
            }
        }
    }
}
