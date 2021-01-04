package de.srsoftware.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

public class Tag extends HashMap<String,String>{

	private static final long serialVersionUID = 6465579367381778798L;
	private Vector<Tag> children = new Vector<Tag>();
	private String type;

	public Tag(String type) {
		this.type = type;
	}
	
	public <T extends Tag> T addTo(T tag) {
		tag.children().add(this);
		return tag;
		
	}

	public Tag alt(String txt) {
		return attr("alt",txt);
	}

	public Tag attr(String key, String val) {
		put(key,val);
		return this;
	}
	
	public Tag attr(String key, int i) {
		return attr(key,""+i);
	}
	
	public Vector<Tag> children() {
		return children;
	}

	public Tag clazz(Collection<String> classes) {
		put("class",String.join(" ", classes));
		return this;
	}
	
	public Tag clazz(String...classes) {
		put("class",String.join(" ", classes));
		return this;
	}

	public Tag content(String content) {
		return new Tag(null) {
			private static final long serialVersionUID = 1L;

			@Override
			public String toString() {
				return content;
			}
		}.addTo(this);
	}

	public Tag id(String id) {
		return attr("id",id);
	}
	
	public boolean is(String type) {
		return this.type.equalsIgnoreCase(type);
	}
	
	public Tag pos(int x, int y) {
		return attr("x",x).attr("y", y);
	}

	public Tag size(int width, int height) {
		return attr("width",width).attr("height",height);
	}

	public Tag style(String style) {
		return attr("style",style);
	}

	public Tag title(String t) {
		return attr("title",t);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<"+type);
		for (Entry<String, String> entry : entrySet()) sb.append(" "+entry.getKey()+"=\""+entry.getValue()+"\"");
		if (children.isEmpty()) {
			sb.append(" />");
		} else {
			sb.append(">");
			for (Tag child : children) {
				sb.append(child.toString());				
			}
			sb.append("</"+type+">");
		}

		return sb.toString();
	}
}
