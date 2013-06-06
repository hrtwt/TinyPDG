package yoshikihigo.tinypdg.scorpio;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import yoshikihigo.tinypdg.pdg.PDG;
import yoshikihigo.tinypdg.pdg.edge.PDGEdge;

public class HashCalculationThread implements Runnable {

	final static private AtomicInteger INDEX = new AtomicInteger(0);

	final private PDG[] pdgs;
	final private SortedMap<PDG, SortedMap<PDGEdge, Integer>> mappingPDGToPDGEdges;

	public HashCalculationThread(
			final PDG[] pdgs,
			final SortedMap<PDG, SortedMap<PDGEdge, Integer>> mappingPDGToPDGEdges) {

		assert null != pdgs : "\"pdgs\" is null.";
		assert null != mappingPDGToPDGEdges : "\"mappingPDGToPDGEdges\" is null.";

		this.pdgs = pdgs;
		this.mappingPDGToPDGEdges = mappingPDGToPDGEdges;
	}

	@Override
	public void run() {

		for (int index = INDEX.getAndIncrement(); index < this.pdgs.length; index = INDEX
				.getAndIncrement()) {

			final PDG pdg = this.pdgs[index];
			final SortedMap<PDGEdge, Integer> mappingPDGEdgeToHash = new TreeMap<PDGEdge, Integer>();
			for (final PDGEdge edge : pdg.getAllEdges()) {

				final NormalizedText t1 = new NormalizedText(edge.fromNode.core);
				final String fromNodeText = NormalizedText.normalize(t1
						.getText());
				final NormalizedText t2 = new NormalizedText(edge.toNode.core);
				final String toNodeText = NormalizedText
						.normalize(t2.getText());
				final StringBuilder edgeText = new StringBuilder();
				edgeText.append(fromNodeText);
				edgeText.append("-");
				edgeText.append(edge.type.toString());
				edgeText.append("->");
				edgeText.append(toNodeText);
				final int hash = edgeText.toString().hashCode();

				mappingPDGEdgeToHash.put(edge, hash);
			}
			this.mappingPDGToPDGEdges.put(pdg, mappingPDGEdgeToHash);
		}
	}
}