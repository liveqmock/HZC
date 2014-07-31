package com.haozan.caipiao.jgravatar;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A gravatar is a dynamic image resource that is requested from the
 * gravatar.com server. This class calculates the gravatar url and fetches
 * gravatar images. See http://en.gravatar.com/site/implement/url .
 * 
 * This class is thread-safe, Gravatar objects can be shared.
 * 
 * Usage example:
 * 
 * <code>
 * Gravatar gravatar = new Gravatar();
 * gravatar.setSize(50);
 * gravatar.setRating(GravatarRating.GENERAL_AUDIENCES);
 * gravatar.setDefaultImage(GravatarDefaultImage.IDENTICON);
 * String url = gravatar.getUrl("iHaveAn@email.com");
 * byte[] jpg = gravatar.download("info@ralfebert.de");
 * </code>
 */
public final class Gravatar {

	private final static int DEFAULT_SIZE = 80;
	private final static String GRAVATAR_URL = "http://www.gravatar.com/avatar/";
	private static final GravatarRating DEFAULT_RATING = GravatarRating.GENERAL_AUDIENCES;
	private static final GravatarDefaultImage DEFAULT_DEFAULT_IMAGE = GravatarDefaultImage.HTTP_404;

	private int size = DEFAULT_SIZE;
	private GravatarRating rating = DEFAULT_RATING;
	private GravatarDefaultImage defaultImage = DEFAULT_DEFAULT_IMAGE;

	/**
	 * Specify a gravatar size between 1 and 512 pixels. If you omit this, a
	 * default size of 80 pixels is used.
	 */
	public void setSize(int sizeInPixels) {
		this.size = sizeInPixels;
	}

	/**
	 * Specify a rating to ban gravatar images with explicit content.
	 */
	public void setRating(GravatarRating rating) {
		this.rating = rating;
	}

	/**
	 * Specify the default image to be produced if no gravatar image was found.
	 */
	public void setDefaultImage(GravatarDefaultImage defaultImage) {
		this.defaultImage = defaultImage;
	}

	/**
	 * Returns the Gravatar URL for the given email address.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public String getUrl(String email) throws Exception {
		// hexadecimal MD5 hash of the requested user's lowercased email address
		// with all whitespace trimmed
		byte[] data = MessageDigest.getInstance("MD5").digest(
				email.toLowerCase().getBytes("UTF-8"));
		int l = data.length;
		char[] out = new char[l << 1];
		char[] toDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		int i = 0;
		for (int j = 0; i < l; ++i) {
			out[(j++)] = toDigits[((0xF0 & data[i]) >>> 4)];
			out[(j++)] = toDigits[(0xF & data[i])];
		}
		String emailHash = new String(out);
		String params = formatUrlParameters();
		return GRAVATAR_URL + emailHash + ".jpg" + params;
	}

	/**
	 * Downloads the gravatar for the given URL using Java {@link URL} and
	 * returns a byte array containing the gravatar jpg, returns null if no
	 * gravatar was found.
	 */
	public InputStream download(String email) {
		try {
			URL url = new URL(getUrl(email));
			return url.openStream();
		} catch (Exception e) {
			return null;
		}
	}

	private String formatUrlParameters() {
		List<String> params = new ArrayList<String>();
		if (size != DEFAULT_SIZE)
			params.add("s=" + size);
		if (rating != DEFAULT_RATING)
			params.add("r=" + rating.getCode());
		if (defaultImage != GravatarDefaultImage.GRAVATAR_ICON)
			params.add("d=" + defaultImage.getCode());
		if (params.isEmpty())
			return "";
		else {
			String separator = "&";
			Iterator<String> iterator = params.iterator();
			if (iterator == null)
				return null;
			if (!(iterator.hasNext()))
				return "";
			String first = iterator.next();
			if (!(iterator.hasNext()))
				return first;
			StringBuffer buf = new StringBuffer(256);
			if (first != null)
				buf.append(first);
			while (iterator.hasNext()) {
				if (separator != null)
					buf.append(separator);
				Object obj = iterator.next();
				if (obj != null)
					buf.append(obj);
			}
			return "?" + buf.toString();
		}
	}

	public static void main(String[] args) throws IOException {
		Gravatar g = new Gravatar();
		BufferedInputStream bis = new BufferedInputStream(g
				.download("cwalet@163.com"));
		FileOutputStream fos = new FileOutputStream("c:/1.jpg");
		byte[] bs = new byte[1024];
		while (bis.read(bs) != -1)
			fos.write(bs);
		fos.close();
	}
}