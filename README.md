# Convert PPD CSV data to NQUADS

The conversion processes produces a complete TDB/Lucene image that can be served by Fuseki.

The Java code in this project will convert a complete PPD CSV file into NQUADS.  Vocabulary,
HPI data, and TRANS data in TTL format are converted to QUADS and loaded into the image.

The directory layout is as follows:

/media/ephemeral0/image-build/
  csv          # PPD CSV files
  ttl          # HPI and TRANS ttl files
  vocab        # owl files in .ttl format
  nq           # converted NQUADS files to be loaded into the image
  LR-DB        # TDB image
  LR-DB-lucene # text index
  TEMP         # temp directory - needed by tdbloader2 for large sorts
  
  
## scripts

The image build process is executed by the script image-build.  image-build will, in general
not rerun conversions if there is already a converted file.  The script image-clean will
remove all converted files and start again from scratch.

image-build simply runs a series of scripts that perform one operation one after the other.
These scripts can be run individually if desired.

The scripts are normally manually copied to /usr/local/bin.  They are not installed by Chef.
The jar-with-dependencies file is manually copied to /home/ubuntu/ppd-converter.jar.
