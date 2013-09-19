classes for importing data

ImportPSIMI imports PPI data from a PSI-MI TAB 2.7 file, with KBase-
  specific extensions described in the comments at the beginning of the
  code.

example usage:

# this the minimal classpath for compiling this code:
setenv CLASSPATH /kb/dev_container/modules/networks/lib/strbio-1.2.jar:/kb/dev_container/modules/networks/lib/c3p0-0.9.2-pre7.jar:/kb/dev_container/modules/networks/lib/mysql-connector-java-5.1.22-bin.jar:/kb/dev_container/modules/networks/src

java us.kbase.networks.adaptor.ppi.importer.ImportPSIMI psi_mi_file.csv

acknowledgments for current datasets that are imported:

The following Protein-Protein Interaction datasets are available in
the Interactions region of the KBase Central Store:

BioGRID 2.0: Stark C, Breitkreutz BJ, Reguly T, Boucher L, Breitkreutz
A, Tyers M. Biogrid: A General Repository for Interaction
Datasets. Nucleic Acids Res. Jan1; 34:D535-9

EcoCyc 16.1: Keseler, I.M., Collado-Vides, J., Santos-Zavaleta, A.,
Peralta-Gil, M., Gama-Castro, S., Muniz-Rascado, L.,
Bonavides-Martinez, C., Paley, S., Krummenacker, M., Altman, T.,
Kaipa, P., Spaulding, A., Pacheco, J., Latendresse, M., Fulcher, C.,
Sarker, M., Shearer, A.G., Mackie, A., Paulsen, I., Gunsalus, R.P.,
and Karp, P.D., EcoCyc: a comprehensive database of Escherichia coli
biology Nucleic Acids Research 39:D583-590 2011.

IntAct 2013-08-30: Kerrien SAranda B, Breuza L, Bridge A,
Broackes-Carter F, Chen C, Duesbury M, Dumousseau M, Feuermann M, Hinz
U, , et al. The IntAct molecular interaction database in 2012.
Nucleic Acids Res. 2012 Jan; 40(Database issue):D841-6. Epub 2011 Nov
24.
