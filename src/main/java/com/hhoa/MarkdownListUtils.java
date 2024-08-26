package com.hhoa;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class MarkdownListUtils {
    private final List<Node> rootNodes = new ArrayList<>();
    private int subGraphId = 0;

    public String convertToMermaid(String list) {
        generateRootNodes(list);
        StringBuilder mermaid = new StringBuilder();
        appendGraphHeader(mermaid);
        Node node = new Node(-1);
        node.id = -1;
        node.subNodes = rootNodes;
        writeSubNode(mermaid, node);
        appendGraphEnd(mermaid);
        return mermaid.toString();
    }

    private void writeSubNode(StringBuilder mermaid, Node node) {
        List<Node> subNodes = node.subNodes;
        if (!subNodes.isEmpty()) {
            int id = node.id;
            if (id != -1) {
                appendGraphLink(mermaid, id, subNodes.get(0).id);
            }
            if (subNodes.size() > 1) {
                appendSubGraphHeader(mermaid);
            }
            for (int i = 0; i < subNodes.size(); i++) {
                Node subNode = subNodes.get(i);
                appendSubGraphContent(mermaid, subNode.content, subNode.id);
                if (i < subNodes.size() - 1) {
                    appendNodePoint(mermaid);
                }
            }
            if (subNodes.size() > 1) {
                appendSubGraphEnd(mermaid);
            }
            for (Node rootNode : subNodes) {
                writeSubNode(mermaid, rootNode);
            }
        }
    }

    private void generateRootNodes(String markdown) {
        int space = 0;
        ArrayDeque<Node> nodeRoute = new ArrayDeque<>();
        StringBuilder currentContent = new StringBuilder();
        boolean bold = false, code = false, indent = true, escape = false;
        for (int i = 0; i < markdown.length(); i++) {
            char c = markdown.charAt(i);
            if (c == ' ') {
                if (indent) {
                    space++;
                    continue;
                }
            } else if (indent) {
                if (i < markdown.length() - 1 && markdown.charAt(i) != '\n') {
                    currentContent.append("\n");
                    indent = false;
                }
            }

            if (c == '*') {
                if (i < markdown.length() - 1 && (markdown.charAt(i+1)) == ' ') {
                    i++;
                    Node node = new Node(space);
                    if (!nodeRoute.isEmpty()) {
                        Node peek = nodeRoute.peek();
                        peek.content = currentContent.toString();
                    }
                    while (!nodeRoute.isEmpty()) {
                        Node peek = nodeRoute.peek();
                        if (peek.space == node.space) {
                            if (peek.parent != null) {
                                node.parent = peek.parent;
                                peek.parent.subNodes.add(node);
                            }
                            nodeRoute.pop();
                            break;
                        } else if (peek.space < node.space) {
                            node.parent = peek;
                            peek.subNodes.add(node);
                            break;
                        } else {
                            nodeRoute.pop();
                        }
                    }
                    if (nodeRoute.isEmpty()) {
                        rootNodes.add(node);
                        nodeRoute.push(node);
                    } else {
                        nodeRoute.push(node);
                    }
                    currentContent = new StringBuilder();
                } else if (i < markdown.length() - 1 && (markdown.charAt(i+1)) == '*') {
                    i++;
                    if (bold) {
                        currentContent.append("</b>");
                    } else {
                        currentContent.append("<b>");
                    }
                    bold = !bold;
                }
            } else if (c == '\\') {
                escape = true;
            } else if (c == '`') {
                if (code) {
                    currentContent.append("</code>");
                } else {
                    currentContent.append("<code>");
                }
                code = !code;
            } else if (c == '\n') {
                space = 0;
                indent = true;
            } else {
                currentContent.append(c);
            }
        }
        if (!nodeRoute.isEmpty()) {
            Node pop = nodeRoute.pop();
            pop.content = currentContent.toString();
        }
    }

    private void appendGraphLink(StringBuilder mermaid, int parentId, int childId) {
        mermaid
            .append("\n")
            .append(parentId)
            .append(" --> ")
            .append(childId)
            .append("\n")
            .append("\n");
    }

    private void appendNodePoint(StringBuilder mermaid) {
        mermaid.append(" --> \n");
    }

    private void appendGraphHeader(StringBuilder mermaid) {
        mermaid
            .append("```mermaid\n")
            .append("flowchart TD")
            .append("\n");
    }

    private void appendGraphEnd(StringBuilder mermaid) {
        mermaid.append("\n```");
    }

    private void appendSubGraphContent(StringBuilder mermaid, String currentContent, int nodeId) {
        mermaid
            .append(nodeId)
            .append("(\"")
            .append(currentContent)
            .append("\")");
    }

    private void appendSubGraphEnd(StringBuilder mermaid) {
        mermaid
            .append("\n")
            .append("end")
            .append("\n");
    }

    private void appendSubGraphHeader(StringBuilder mermaid) {
        mermaid
            .append("subgraph " + "SG")
            .append(subGraphId++)
            .append(" [\" \"]")
            .append("\n");
    }

    static class Node {
        static int nodeId = 0;
        int id;
        Node parent;
        List<Node> subNodes = new ArrayList<>();
        int space;
        String content = "";

        Node(int space) {
            this.id = nodeId++;
            this.space = space;
        }
    }
}