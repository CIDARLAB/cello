package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.Logger;

/***********************************************************************
 * 
 * Synopsis [ Functions to generate output DNA sequence. A plasmid object is an
 * ArrayList of Parts. ]
 * 
 * Plasmids are build differently for TetR biochemistry and CRISPRi
 * biochemistry. CRISPRi does not allow tandem promoters, and instead separates
 * a NOR gate into two separate transcriptional units.
 * 
 ***********************************************************************/

public class PlasmidUtil {

	/**
	 * Printing a plasmid = printing all parts (name, type, direction, start, end)
	 */
	public static String printPlasmid(ArrayList<Part> plasmid) {
		String s = "";

		for (Part part : plasmid) {
			s += part.toString() + "\n";
		}

		return s;
	}

	public static String extractNucleotideSequenceFromGenbankLines(ArrayList<String> file_lines) {

		String fulltext = "";
		String nucleotides = "";

		for (String line : file_lines) {
			fulltext += line + "\n";
		}

		String[] annotation_and_sequence = fulltext.split("ORIGIN");

		ArrayList<String> sequence_tokens = Util.lineTokenizer(annotation_and_sequence[1]);

		for (String token : sequence_tokens) {
			if (isDNA(token)) {
				nucleotides += token;
			}
		}

		return nucleotides;

	}

	public static boolean isDNA(String name) {
		return name.matches("[atcgATCG]+");
	}

	public static ArrayList<String> writePlasmidFiles(ArrayList<ArrayList<Part>> plasmids, String prefixA, String label,
			String directory) {

		ArrayList<String> plasmid_strings = new ArrayList<String>();

		for (int i = 0; i < plasmids.size(); ++i) {

			ArrayList<Part> plasmid = plasmids.get(i);

			String fout = prefixA + "_" + label + "_P" + String.format("%03d", i) + ".ape";

			PlasmidUtil.renumberPlasmidBases(plasmid, 0);

			String plasmid_string = getApe(fout, plasmid);
			plasmid_strings.add(plasmid_string);

			Util.fileWriter(directory + fout, plasmid_string, false);

		}

		return plasmid_strings;
	}

	public static ArrayList<String> writePlasmidFiles1(ArrayList<ArrayList<Part>> plasmids, String prefixA,
			String label, String directory, Args options, PartLibrary part_library) {
		log.info("\n=========== Writing plasmid files ============");

		ArrayList<String> plasmid_strings = new ArrayList<String>();

		for (int i = 0; i < plasmids.size(); ++i) {

			ArrayList<Part> rename_plasmid = new ArrayList<Part>();

			ArrayList<Part> plasmid = plasmids.get(i);

			if (options.is_yeast() || options.is_genome()) {

				ArrayList<String> partnames = new ArrayList<String>();

				for (Part part : plasmid) {
					String part_name = part.get_name();
					if (partnames.contains(part_name) && !part_name.equals("YFP") && !part_name.equals("backbone")) {
						String temp = part_name.replace("_a", "_b");
						part_name = temp;

						part.set_name(part_name);
						part.set_seq(part_library.get_ALL_PARTS().get(part_name).get_seq());

						rename_plasmid.add(part);
					}

					else {
						rename_plasmid.add(part);
					}
					partnames.add(part_name);

				}

				int separate = 0;

				for (int j = 0; j < rename_plasmid.size(); j++) {
					Part part = rename_plasmid.get(j);
					if (part.get_name().equals("Jscar")) {
						separate = j;
					}
				}

				ArrayList<Part> plasmid1 = new ArrayList<Part>(rename_plasmid.subList(0, separate));
				ArrayList<Part> plasmid2 = new ArrayList<Part>(rename_plasmid.subList(separate, rename_plasmid.size()));

				String fout1 = prefixA + "_" + label + "_P" + String.format("%03d", i) + "_1.ape";
				String fout2 = prefixA + "_" + label + "_P" + String.format("%03d", i) + "_2.ape";

				String plasmid_string1 = getApe(fout1, plasmid1);
				String plasmid_string2 = getApe(fout2, plasmid2);

				Util.fileWriter(directory + fout1, plasmid_string1, false);
				Util.fileWriter(directory + fout2, plasmid_string2, false);
			}

			else {
				rename_plasmid = plasmid;
			}

			String fout = prefixA + "_" + label + "_P" + String.format("%03d", i) + ".ape";

			PlasmidUtil.renumberPlasmidBases(rename_plasmid, 0);

			String plasmid_string = getApe(fout, rename_plasmid);
			plasmid_strings.add(plasmid_string);

			Util.fileWriter(directory + fout, plasmid_string, false);
			log.info("Write file: " + fout);

		}

		return plasmid_strings;
	}

	/***********************************************************************
	 * 
	 * Concatenate all parts into a single DNA sequence. Annotate parts.
	 * 
	 * scar 1857..1860 /label=F-scar /ApEinfo_fwdcolor=gray /ApEinfo_revcolor=gray
	 * promoter 1861..1931 /label=input_pTac /ApEinfo_fwdcolor=green
	 * /ApEinfo_revcolor=green insulator 1932..2010 /label=RiboJ53
	 * /ApEinfo_fwdcolor=magenta /ApEinfo_revcolor=magenta rbs 2011..2043
	 * /label=RBS0 /ApEinfo_fwdcolor=blue /ApEinfo_revcolor=blue cds 2044..2646
	 * /label=PhlF /ApEinfo_fwdcolor=cyan /ApEinfo_revcolor=cyan terminator
	 * 2647..2703 /label=ECK120033737 /ApEinfo_fwdcolor=red /ApEinfo_revcolor=red
	 * 
	 * ORIGIN 1 gcttcctcgc tcactgactc gctgcacgag gcagacctca gcgctagcgg agtgtatact 61
	 * ggcttactat gttggcactg atgagggtgt cagtgaagtg cttcatgtgg caggagaaaa 121
	 * aaggctgcac cggtgcgtca gcagaatatg tgatacagga tatattccgc ttcctcgctc 181
	 * actgactcgc tacgctcggt cgttcgactg cggcgagcgg aaatggctta cgaacggggc 241
	 * ggagatttcc tggaagatgc caggaagata cttaacaggg aagtgagagg gccgcggcaa
	 * 
	 ***********************************************************************/

	public static String getApe(String fout_ape, ArrayList<Part> plasmid) {
		String s_ape = "";
		// 1. connect all the DNA sequences
		String seq = "";
		for (Part part : plasmid) {

			/////////////////////////////////////////////////////////////////////////////////////////////////
			// NOTE: this is where the part gets the reverse complement sequence if in
			///////////////////////////////////////////////////////////////////////////////////////////////// reverse
			///////////////////////////////////////////////////////////////////////////////////////////////// orientation
			// changing the actual part.get_seq() object could cause unwanted
			///////////////////////////////////////////////////////////////////////////////////////////////// double-reversing

			if (part.get_direction().equals("+"))
				seq = seq.concat(part.get_seq());
			else if (part.get_direction().equals("-"))
				seq = seq.concat(UtilDNA.getReverseComplement(part.get_seq()));

			/////////////////////////////////////////////////////////////////////////////////////////////////

		}

		// 2. write LOCUS
		s_ape += String.format("LOCUS       %-24s%6d bp ds-DNA   circular%16s\n", UtilDNA.getBaseName(fout_ape),
				seq.length(), UtilDNA.getDate4GenBank());

		// 3. write FEATURES
		s_ape += "FEATURES             Location/Qualifiers\n";
		int current_bp = 0;
		for (Part part : plasmid) {

			int start = current_bp + 1;
			current_bp += part.get_seq().length();
			int end = current_bp;

			part.set_start(start);
			part.set_end(end);

			if (part.get_ape_color().length() != 0) {
				s_ape += String.format("     %-16s%d..%d\n", part.get_type(), start, end);
				s_ape += "                     /label=" + part.get_name() + "\n";
				s_ape += "                     /ApEinfo_fwdcolor=" + part.get_ape_color() + "\n";
				s_ape += "                     /ApEinfo_revcolor=" + part.get_ape_color() + "\n";
			}
		}

		// 4. write ORIGIN and DNA sequence
		s_ape += "ORIGIN\n";
		int n_seq_lines = seq.length() / 60; // 60 bp per line
		for (int i = 0; i <= n_seq_lines; ++i) {
			s_ape += String.format("%9d", 60 * i + 1);
			for (int j = 0; j < 6; ++j) {
				int end = 60 * i + 10 * (j + 1);
				if (end <= seq.length()) {
					s_ape += " " + seq.substring(60 * i + 10 * j, 60 * i + 10 * (j + 1));
				} else {
					s_ape += " " + seq.substring(60 * i + 10 * j, seq.length());
					break;
				}
			}
			s_ape += "\n";
		}
		s_ape += "//";
		s_ape += "\n";
		s_ape += "\n";
		s_ape += "\n";

		return s_ape;
	}

	/**
	 *
	 * set_start and set_end of parts in plasmid
	 *
	 */
	public static void renumberPlasmidBases(ArrayList<Part> parts, int start_bp) {

		int current_bp = start_bp;
		for (Part part : parts) {

			int start = current_bp + 1;
			current_bp += part.get_seq().length();
			int end = current_bp;

			part.set_start(start);
			part.set_end(end);
		}
	}

	public static void resetParentGates(LogicCircuit lc) {
		for (ArrayList<Part> plasmid : lc.get_circuit_plasmid_parts()) {
			for (Part p : plasmid) {
				for (Gate g : lc.get_Gates()) {

					if (g.type != Gate.GateType.OUTPUT && g.type != Gate.GateType.OUTPUT_OR) {
						if (p.get_name().equals(g.get_regulable_promoter().get_name())) {
							p.set_parent_gate(g);
						}
					}

					if (g.type != Gate.GateType.INPUT) {

						for (String var : g.get_downstream_parts().keySet()) {
							ArrayList<Part> cassette = g.get_downstream_parts().get(var);
							for (Part cp : cassette) {
								if (p.get_name().equals(cp.get_name())) {
									p.set_parent_gate(g);
								}
							}
						}

					}

				}
			}
		}
	}

	public static void findPartComponentsInOutputGates(LogicCircuit lc, GateLibrary gate_library,
			PartLibrary part_library) {

		for (int i = 0; i < lc.get_output_gates().size(); ++i) {
			Gate g = lc.get_output_gates().get(i);

			// see if the output sequence (specified as a concatenated sequence) contains
			// multiple Parts.
			// if so, set the downstream parts accordingly, instead of a single composite
			// part
			int largest_match = 0;
			ArrayList<Part> output_parts = new ArrayList<>();
			boolean found_part_at_prefix = true;
			String output_seq = gate_library.get_OUTPUTS_SEQ().get(g.name);

			while (found_part_at_prefix) {

				boolean found_this = false;

				for (Part p : part_library.get_ALL_PARTS().values()) {

					String part_seq = p.get_seq();

					if (output_seq.toUpperCase().startsWith(part_seq.toUpperCase())) {

						found_this = true;

						if (part_seq.length() > largest_match) {
							largest_match = part_seq.length();
							output_parts.add(p);

							String new_seq = output_seq.substring(part_seq.length(), output_seq.length());
							output_seq = new_seq;

							if (output_seq.length() == 0) {
								found_this = false;
							}

						}
					}
				}

				if (!found_this) {
					found_part_at_prefix = false;
				}

				largest_match = 0;
			}

//            for(Part p: output_parts) {
//                System.out.println("found this output part: " + p.toString());
//            }

			if (output_parts.isEmpty()) {

				throw new IllegalStateException("output_parts is empty, this is an error! ");

				/*
				 * ArrayList<Part> composite_part = new ArrayList<Part>();
				 * 
				 * composite_part.add( new Part(g.Name, "output",
				 * gate_library.get_OUTPUTS_SEQ().get(g.Name)) );
				 * 
				 * part_library.get_ALL_PARTS().put(g.Name, composite_part.get(0));
				 * 
				 * g.get_downstream_parts().put("x", composite_part);
				 */
			} else {
				g.get_downstream_parts().put("x", output_parts);
			}

		}

	}

	/**
	 * For logic gates, point gate_parts to those already constructed
	 *
	 * For input/output gates, make parts based on name, type, sequence.
	 *
	 * Must be done before building plasmid(s).
	 *
	 */
	public static void setGateParts(LogicCircuit lc, GateLibrary gate_library, PartLibrary part_library) {

		for (int i = 0; i < lc.get_logic_gates().size(); ++i) {
			Gate g = lc.get_logic_gates().get(i);

			if (gate_library.get_GATES_BY_NAME().containsKey(g.name)) {

				g.set_downstream_parts(gate_library.get_GATES_BY_NAME().get(g.name).get_downstream_parts());

				g.set_regulable_promoter(gate_library.get_GATES_BY_NAME().get(g.name).get_regulable_promoter());
			}

		}

		for (int i = 0; i < lc.get_input_gates().size(); ++i) {
			Gate g = lc.get_input_gates().get(i);

			Part input_promoter = new Part(g.name, "promoter", gate_library.get_INPUTS_SEQ().get(g.name));

			part_library.get_ALL_PARTS().put(g.name, input_promoter);

			g.set_regulable_promoter(input_promoter);
		}

		for (int i = 0; i < lc.get_output_gates().size(); ++i) {

			Gate g = lc.get_output_gates().get(i);

			ArrayList<Part> composite_part = new ArrayList<Part>();

			composite_part.add(new Part(g.name, "output", gate_library.get_OUTPUTS_SEQ().get(g.name)));

			if (!part_library.get_ALL_PARTS().containsKey(g.name)) {
				part_library.get_ALL_PARTS().put(g.name, composite_part.get(0));
			}

			g.get_downstream_parts().put("x", composite_part);
		}

		// set parent gates

		for (Gate g : lc.get_input_gates()) {

			g.get_regulable_promoter().set_parent_gate(g);

		}

		for (Gate g : lc.get_logic_gates()) {

			g.get_regulable_promoter().set_parent_gate(g);

			for (String var : g.get_downstream_parts().keySet()) {
				ArrayList<Part> parts = g.get_downstream_parts().get(var);
				for (Part p : parts) {
					p.set_parent_gate(g);
				}
			}
		}

		for (Gate g : lc.get_output_gates()) {

			for (String var : g.get_downstream_parts().keySet()) {
				ArrayList<Part> parts = g.get_downstream_parts().get(var);
				for (Part p : parts) {
					p.set_parent_gate(g);
				}
			}

		}

	}

	/**
	 * wire the promoters according to circuit.
	 */
	public static void setTxnUnits(LogicCircuit lc, GateLibrary gate_library, Args _options) {

		ArrayList<Gate> gates = new ArrayList<Gate>();

		gates.addAll(lc.get_logic_gates());

		gates.addAll(lc.get_output_gates());

		for (Gate g : gates) {

			if (!_options.is_tandem_NOR()) {

				for (String var : g.get_variable_names()) {

					for (Wire w : g.get_variable_wires().get(var)) {
						ArrayList<Part> txn_unit = new ArrayList<>();

						txn_unit.add(w.to.get_regulable_promoter());

						ArrayList<Part> expression_cassette = g.get_downstream_parts().get(var);

						txn_unit.addAll(expression_cassette);

						g.get_txn_units().add(txn_unit);
					}
				}
			}

			else {

				boolean f1 = g.getChildren().size() > 0
						&& (g.system.equals("CRISPRi") || g.getChildren().get(0).system.equals("CRISPRi")
								|| g.system.equals("Ecoligenome") || g.getChildren().get(0).system.equals("Ecoligenome")
								|| g.system.equals("Yeast") || g.getChildren().get(0).system.equals("Yeast"));

				boolean f2 = g.getChildren().size() > 1 && (g.getChildren().get(1).system.equals("Ecoligenome")
						|| g.getChildren().get(1).system.equals("Yeast"));

				/*
				 * if(g.system.equals("CRISPRi") ||
				 * g.getChildren().get(0).system.equals("CRISPRi") ||
				 * g.system.equals("Ecoligenome") ||
				 * g.getChildren().get(0).system.equals("Ecoligenome") ||
				 * g.getChildren().get(1).system.equals("Ecoligenome") ||
				 * g.system.equals("Yeast") || g.getChildren().get(0).system.equals("Yeast") ||
				 * g.getChildren().get(1).system.equals("Yeast")) {
				 */
				if (f1 || f2) {

					for (String var : g.get_variable_names()) {

						for (Wire w : g.get_variable_wires().get(var)) {
							ArrayList<Part> txn_unit = new ArrayList<>();

							txn_unit.add(w.to.get_regulable_promoter());

							ArrayList<Part> expression_cassette = g.get_downstream_parts().get(var);

							txn_unit.addAll(expression_cassette);

							g.get_txn_units().add(txn_unit);
						}

					}

					/*
					 * for(Gate child: g.getChildren()) {
					 * 
					 * ArrayList<Part> txn_unit = new ArrayList<>();
					 * 
					 * txn_unit.add(child.get_regulable_promoter());
					 * 
					 * txn_unit.addAll(g.get_downstream_parts().get(0));
					 * 
					 * g.get_txn_units().add(txn_unit); }
					 */
				}

				else if (!g.system.equals("CRISPRi")) {

					for (String var : g.get_variable_names()) {

						ArrayList<Part> txn_unit = new ArrayList<>();

						for (Wire w : g.get_variable_wires().get(var)) {
							txn_unit.add(w.to.get_regulable_promoter());
						}

						ArrayList<Part> expression_cassette = g.get_downstream_parts().get(var);

						txn_unit.addAll(expression_cassette);

						g.get_txn_units().add(txn_unit);
					}
				}
			}

			for (int j = 0; j < g.get_txn_units().size(); ++j) {

				String tu = "";

				for (int i = 0; i < g.get_txn_units().get(j).size(); ++i) {

					Part p = g.get_txn_units().get(j).get(i);

					tu += p.get_name() + " ";

				}

				// Print.message(1, "txn unit: " + tu);
			}

		}

	}

	public static void sortPartsByStartBP(ArrayList<Part> parts) {
		Collections.sort(parts, new Comparator<Part>() {
			public int compare(Part p1, Part p2) {
				int result = 0;
				if (p1.get_start() < p2.get_start()) {
					result = -1;
				} else if (p1.get_start() > p2.get_start()) {
					result = 1;
				} else if (p1.get_start() == p2.get_start()) {
					if (p1.get_end() < p2.get_end()) {
						result = -1;
					} else if (p1.get_end() > p2.get_end()) {
						result = 1;
					}
				}
				return result;
			}
		});
	}

	// static cannot do this.getClass
	private static Logger log = Logger.getLogger(new Object() {
	}.getClass().getEnclosingClass());

};
