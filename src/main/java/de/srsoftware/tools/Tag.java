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
	
	public Tag add(Tag tag) {
		if (tag != null) children().add(tag);
		return this;
	}
	
	public <T extends Tag> T addTo(T tag) {
		tag.children().add(this);
		return tag;
		
	}

	public  <T extends Tag> T alt(String txt) {
		return attr("alt",txt);
	}

	@SuppressWarnings("unchecked")
	public <T extends Tag> T attr(String key, String val) {
		put(key,val);
		return (T) this;
	}
	
	public  <T extends Tag> T attr(String key, int i) {
		return attr(key,""+i);
	}
	
	public Vector<Tag> children() {
		return children;
	}

	@SuppressWarnings("unchecked")
	public <T extends Tag> T clazz(Collection<String> classes) {
		put("class",String.join(" ", classes));
		return (T)this;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Tag> T clazz(String...classes) {
		put("class",String.join(" ", classes));
		return (T)this;
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

	public <T extends Tag> T id(String id) {
		return attr("id",id);
	}
	
	public boolean is(String type) {
		return this.type != null && this.type.equalsIgnoreCase(type);
	}
	
	public <T extends Tag> T pos(int x, int y) {
		return attr("x",x).attr("y", y);
	}

	public <T extends Tag> T size(int width, int height) {
		return attr("width",width).attr("height",height);
	}

	public <T extends Tag> T style(String style) {
		return attr("style",style);
	}

	public <T extends Tag> T title(String t) {
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
	
	public String type() {
		return type;
	}
}
