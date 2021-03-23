using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using VDS.RDF;
using VDS.RDF.Parsing;
using VDS.RDF.Writing;

namespace ServiceController.Entities.KnowledgeService
{
	public class TopBraidEdgSparqlInsertBuilder
	{
		public IGraph Graph { get; internal set; } = new Graph();

		// Example: nlppoctestontology
		public string OntologyName { get; internal set; }

		// Example: nlpknowledgefromappworkflow
		public string WorkflowName { get; internal set; }

		// Example: ontologist
		public string Username { get; internal set; }

		public string RdfTurtleTriples { get; internal set; }

		public TopBraidEdgSparqlInsertBuilder(
			string ontologyName,
			string workflowName,
			string username,
			string rdfTurtleTriples)
		{
			OntologyName = ontologyName;
			WorkflowName = workflowName;
			Username = username;
			RdfTurtleTriples = rdfTurtleTriples;

			if (string.IsNullOrWhiteSpace(RdfTurtleTriples)) return;
			IRdfReader parser = new TurtleParser();
			parser.Load(Graph, new StringReader(RdfTurtleTriples));
		}

		//
		// Builders
		//

		public string BuildSparqlInsertQueryString()
		{
			if (Graph.IsEmpty) return "";

			var list = new List<string> { "INSERT DATA {" };
			list.AddRange(
				from triple
					in Graph.Triples
				let tripleSubject = FormatNodeToNTripleString(triple.Subject, TripleSegment.Subject)
				let triplePredicate = FormatNodeToNTripleString(triple.Predicate, TripleSegment.Predicate)
				let tripleObject = FormatNodeToNTripleString(triple.Object, TripleSegment.Predicate)
				select tripleSubject + " " + triplePredicate + " " + tripleObject + " .");
			list.Add("}");

			return string.Join(" ", list.ToArray());
		}

		// Example: urn:x-evn-tag:nlppoctestontology:nlpknowledgefromappworkflow:ontologist
		public string BuildTopBraidEdgGraphUrn()
		{
			return $"urn:x-evn-tag:{OntologyName}:{WorkflowName}:{Username}";
		}

		//
		// Helpers
		//

		public string FormatNodeToNTripleString(INode node, TripleSegment? segment)
		{
			return node.NodeType switch
			{
				NodeType.Blank => FormatBlankNode((IBlankNode) node, segment),
				NodeType.GraphLiteral => FormatGraphLiteralNode((IGraphLiteralNode) node, segment),
				NodeType.Literal => FormatLiteralNode((ILiteralNode) node, segment),
				NodeType.Uri => FormatUriNode((IUriNode) node, segment),
				NodeType.Variable => FormatVariableNode((IVariableNode) node, segment),
				_ => throw new RdfOutputException(
					WriterErrorMessages.UnknownNodeTypeUnserializable("Node not supported."))
			};
		}

		public string FormatBlankNode(IBlankNode b, TripleSegment? segment)
		{
			if (segment == TripleSegment.Predicate)
				throw new RdfOutputException(
					WriterErrorMessages.BlankPredicatesUnserializable("BlankPredicatesUnserializable"));
			return b.ToString();
		}

		public string FormatGraphLiteralNode(IGraphLiteralNode glit, TripleSegment? segment)
		{
			throw new RdfOutputException(
				WriterErrorMessages.GraphLiteralsUnserializable("GraphLiteralsUnserializable"));
		}

		public string FormatLiteralNode(ILiteralNode l, TripleSegment? segment)
		{
			var output = new StringBuilder();

			output.Append('"');
			var value = l.Value;
			output.Append(value.ToCharArray());
			output.Append('"');

			if (!l.Language.Equals(string.Empty))
			{
				output.Append('@');
				output.Append(l.Language.ToLower());
			}
			else if (l.DataType != null)
			{
				output.Append("^^<");
				output.Append(FormatUri(l.DataType));
				output.Append('>');
			}

			return output.ToString();
		}

		public string FormatUriNode(IUriNode u, TripleSegment? segment)
		{
			var output = new StringBuilder();
			output.Append('<');
			output.Append(FormatUri(u.Uri));
			output.Append('>');
			return output.ToString();
		}

		public string FormatVariableNode(IVariableNode v, TripleSegment? segment)
		{
			throw new RdfOutputException(
				WriterErrorMessages.VariableNodesUnserializable("VariableNodesUnserializable"));
		}

		public string FormatUri(Uri u)
		{
			if (!u.IsAbsoluteUri)
			{
				throw new ArgumentException("IRIs to be formatted by the NTriplesFormatter must be absolute IRIs");
			}

			return u.ToString();
		}
	}
}