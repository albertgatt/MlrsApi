package mt.edu.um.mlrs.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import mt.edu.um.util.datastructures.trees.PostorderIterator;
import mt.edu.um.util.datastructures.trees.PreorderIterator;
import mt.edu.um.util.datastructures.trees.Tree;

public class TextNode extends Tree<TextNode> {
	private String _content;
	private Map<String, String> _attributes;
	private String _type;

	public TextNode(String type) {
		super();
		setType(type);
		this._attributes = new TreeMap<String, String>();
	}

	public TextNode(String type, String content) {
		this(type);
		setContent(content);
	}
	
	public boolean hasAttributes() {
		return !this._attributes.isEmpty();
	}
	
	public TextNode copy() {
		TextNode newNode = new TextNode(this._type, this._content);
		
		for(String a: this._attributes.keySet()) {
			newNode.setValue(a, this._attributes.get(a));
		}
		
		for(TextNode child: this.getChildren()) {
			newNode.addChild(child.copy());
		}
		
		return newNode;
	}
	
	public void removeEmptyNodes() {
		Iterator<TextNode> iter = preorder();
		iter.next();
		
		while(iter.hasNext()) {
			TextNode next = iter.next();
		
			if(!next.hasChildren()) {
				String content = StringUtils.cleanWithin(next.getContent());
				
				if(content == null || content.length() == 0) {
					TextNode parent = next.getParent();
					next.delete();
					parent.removeEmptyNodes();
				}
			}
		
		}			
	}
	
	public void delete() {			
		this.parent.children.remove(this);
		this.parent = null;
	}

	public void setValue(String name, String value) {
		this._attributes.put(name, value);
	}

	public String getValue(String attribute) {
		return this._attributes.get(attribute);
	}

	public boolean hasValue(String attribute) {
		return this._attributes.containsKey(attribute);
	}

	public Collection<String> getAttributes() {
		return this._attributes.keySet();
	}

	public void setType(String type) {
		this._type = type;
	}

	public String getType() {
		return this._type;
	}

	public void setContent(String content) {
		this._content = content;
	}

	public String getContent() {
		return this._content;
	}

	public boolean hasContent() {
		return this._content != null;
	}

	public String getContentIncludeChildren() {
		String buffer = "";
		Iterator<TextNode> iter = this.preorder();
		TextNode next;
		
		while(iter.hasNext()) {
			next = iter.next();
			
			if(next.hasContent()) {
				buffer += next.getContent();
			}
		}
		
		return buffer;
	}
	
	public boolean deleteContent() {
		boolean del = this._content != null;
		this._content = null;
		return del;
	}

	public boolean addChild(String type, String content) {
		TextNode child = new TextNode(type, content);
		return super.addChild(child);
	}
	
	public List<TextNode> getChildren(boolean transitive,
			Collection<String> types) {
		List<TextNode> children = new ArrayList<TextNode>();
		Iterator<TextNode> iter;

		if (transitive) {
			iter = preorder();
			//iter.next();
		} else {
			iter = this.children.iterator();
		}

		while (iter.hasNext()) {
			TextNode child = iter.next();
			String childType = child.getType();

			if ((childType != null) && types.contains(childType)) {
				children.add(child);
			}
		}

		return children;
	}

	public List<TextNode> getChildren(boolean transitive, String... types) {
		return getChildren(transitive, Arrays.asList(types));
	}
	
	public TextNode firstChild(String type) {
		List<TextNode> children = getChildren(true, type);		
		return children.isEmpty() ? null : children.get(0);
	}

	public int numChildren(boolean transitive, String type) {
		return this.getChildren(transitive, type).size();
	}

	@Override
	public Iterator<TextNode> postorder() {
		return new PostorderIterator<TextNode>(this);
	}

	@Override
	public Iterator<TextNode> preorder() {
		return new PreorderIterator<TextNode>(this);
	}

	@Override
	public boolean equals(Object o) {
		boolean eq = false;

		if (o instanceof TextNode) {
			TextNode node = (TextNode) o;
			eq = this._type.equals(node._type)
					&& this._attributes.equals(node._attributes)
					&& (this._content == node._content || this._content.equals(node._content))
					&& this.children.equals(node.children);

		}

		return eq;

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this._type);
		builder.append(this._attributes.toString());
		builder.append("[");
		builder.append(this._content);
		builder.append("]");
		return builder.toString();
	}

}
