package de.amo.tools;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Autor: cle
 * Date : 03.06.2008
 * Time : 15:39:49
 */
public class ZipArchiver {

    private StringBuffer sb = new StringBuffer();
    private int maxMoves = -1;
    private int sleepyValueInMillis = -1;
    private File archive;

    private ZipArchiver() {}
    private ZipArchiver(File archive) {
        this.archive = archive;
    }

    public static StringBuffer run( boolean testModus, File archivDir, File[] vetoFileArray ) throws Exception {
        return run( testModus, archivDir, vetoFileArray, -1, 200 );
    }

    public static StringBuffer run( boolean testModus, File archivDir, File[] vetoFileArray, int maxMoves, int sleepyValueInMillis ) throws Exception {
        ZipArchiver archiver = new ZipArchiver();

        archiver.maxMoves = maxMoves;
        archiver.sleepyValueInMillis = sleepyValueInMillis;
        archiver.packLeavesOfArchiveDirButNotTheCurrentArchives( testModus, archivDir, vetoFileArray  );

        return archiver.sb;
    }

    public static StringBuffer runRecursive( boolean testModus, File archivDir, File[] vetoFileArray ) throws Exception {
        return runRecursive( testModus, archivDir, vetoFileArray, -1, 200 );
    }

    public static StringBuffer runRecursive( boolean testModus, File archivDir, File[] vetoFileArray, int maxMoves, int sleepyValueInMillis ) throws Exception {
        ZipArchiver archiver = new ZipArchiver(archivDir);

        archiver.maxMoves = maxMoves;
        archiver.sleepyValueInMillis = sleepyValueInMillis;
        while( archiver.packLeavesOfArchiveDirButNotTheCurrentArchives( testModus, archivDir, vetoFileArray  ) ) {}

        return archiver.sb;
    }


    private void println(String log) {
        sb.append(log + "\n");
    }


    public static File[] listNamedNodes(File file, String patternString) {
        List resultList = new ArrayList();

        if (patternString.equals(file.getName()) ) {
            resultList.add( file );
        }

        File[] subDirs = getSubDirs(file);
        for (int i = 0; i < subDirs.length; i++) {
            File subDir = subDirs[i];
            File[] files = listNamedNodes(subDir, patternString);
            for (int j = 0; j < files.length; j++) {
                resultList.add(files[j]);
            }
        }

        return (File[])resultList.toArray( new File[] {} );
    }

    private boolean packLeavesOfArchiveDirButNotTheCurrentArchives(boolean testModus, File archivDir, File[] vetoFiles) throws Exception {

        if (maxMoves==0) {
            println(maxMoves+ " Operationen verbraucht");
            return false;
        }

        println( "Archiviere: " + archivDir.getAbsolutePath() );

        boolean result = false;

        if ( !archivDir.isDirectory() )       { return false; }
        if ( isFileIn(archivDir, vetoFiles) ) { return false; }

        boolean hasDataFiles = hasDirDataFiles( archivDir );
        boolean hasSubDirs   = hasDirSubDirs  ( archivDir );

        boolean isLeaf       = hasDataFiles &&  !hasSubDirs;
        boolean isEmptyLeaf  = !hasDataFiles && !hasSubDirs;

        if ( !isLeaf && hasSubDirs ) {
            File[] subdirs = getSubDirs( archivDir );
            for (int i = 0; i < subdirs.length; i++) {
                File subdir = subdirs[i];
                if ( packLeavesOfArchiveDirButNotTheCurrentArchives( testModus, subdir, vetoFiles ) ) {
                    result = true;
                }
            }
        }

        if ( isLeaf && hasDataFiles && !archivDir.equals(this.archive)) {
            File parentFile = archivDir.getParentFile();
            File newFile = new File(parentFile.getAbsolutePath(), archivDir.getName() + ".zip");

            if (newFile.exists()) {
                unzip(newFile);
            }

            if (!testModus) {
                File newArchivFile = new File(archivDir.getAbsolutePath() + "_");
                archivDir.renameTo( newArchivFile );

                archivDir = newArchivFile;
            }

            File[] dataFiles = getDataFilesInDir(archivDir);
            if ( packFilesToArchive( dataFiles, newFile, testModus ) ) {
                archivDir.delete();
                result = true;
            }

        }

        if ( isEmptyLeaf && !testModus  && !archivDir.equals(this.archive)) {
            File newArchivFile = new File(archivDir.getAbsolutePath() + ".emptyDir");
            if ( newArchivFile.createNewFile() ) {
                archivDir.delete();
            }
        }

        return result;
    }

    private void unzip(File newFile) throws IOException {
        ZipFile zip = new ZipFile(newFile);

        String dirName = newFile.getAbsolutePath();
        if (dirName.endsWith(".zip")) {
            dirName = dirName.substring(0, dirName.length() - 4 );
        }

        File outDir = new File(dirName);
        if (!outDir.exists()) {
            outDir.mkdir();
        }


        Enumeration enumeration = zip.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) enumeration.nextElement();
            String name = entry.getName();
            BufferedInputStream bis = new BufferedInputStream( zip.getInputStream( entry ) );

            FileOutputStream fos = new FileOutputStream( outDir +File.separator+ name );

            byte[] buffer = new byte[4096];
            int count;

            while ( ( count = bis.read( buffer, 0, 4096 ) ) !=-1 ) {
                fos.write(buffer);
            }

            bis.close();
            fos.close();
        }

        zip.close();
    }

    private boolean packFilesToArchive(File[] dataFiles, File zipFile, boolean test) throws Exception {
        ZipOutputStream zip = null;

        if (!test) {
            zip = new ZipOutputStream(new FileOutputStream(zipFile));
        }

        Map crcMap  = new HashMap();
        Map fileMap = new HashMap();

        for (int i = 0; i < dataFiles.length; i++) {

            File dataFile = dataFiles[i];

            String logText = "... packe "+ dataFile.getAbsolutePath() + " in " + zipFile.getAbsolutePath() + ".";

            if (test) {
                logText = "... wuerde " + dataFile.getAbsolutePath() + " in " + zipFile.getAbsolutePath() + " packen.";
                continue;
            }

            println( logText );


            if (maxMoves>0) {
                maxMoves--;
            }


            FileInputStream fi = new FileInputStream( dataFile );

            int BUFFER = 4096;

            BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);


            ZipEntry entry = new ZipEntry(dataFile.getName());
            zip.putNextEntry(entry);

            CRC32 crc32 = new CRC32();


            int count;

            byte data[] = new byte[BUFFER];
            while((count = origin.read(data, 0, BUFFER)) != -1) {
               zip.write(data, 0, count);
               crc32.update(data, 0, count );
            }

            if ( sleepyValueInMillis > 0 ) {
                Thread.sleep(sleepyValueInMillis);
            }

            origin.close();

            crcMap.put( dataFile.getName(), Long.toString(crc32.getValue()) );
            fileMap.put( dataFile.getName(), dataFile );

        }

        if (test) {
            return true;
        }

        zip.close();

        ZipFile resultZipFile = new ZipFile(zipFile);
        Enumeration zipEntries = resultZipFile.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipEntries.nextElement();
            String name = entry.getName();
            String crc = Long.toString(entry.getCrc());

            if (crcMap.containsKey(name)) {
                String orginCrc = (String) crcMap.get(name);
                println( "CRC: " + name+ " orginal: " + orginCrc + " im zip: " + crc );
                crcMap.remove(name);
            } else {
                println( name + " war nicht im Umfang der gefundenen Quelldateien");
                return false;
            }
        }

        if ( crcMap.size() != 0 ) {
            println("Es wurden nicht alle Dateien gepackt. Es fehlen:");
            Iterator iterator = crcMap.keySet().iterator();
            while (iterator.hasNext()) {
                String name = (String) iterator.next();
                println(name);
            }
            return false;
        }

        if (crcMap.size()==0) {
            Iterator iterator = fileMap.keySet().iterator();
            List deleted = new ArrayList();
            while (iterator.hasNext()) {
                String dataFileName = (String) iterator.next();
                File dataFile = (File) fileMap.get(dataFileName);
                dataFile.delete();
                deleted.add(dataFileName);
            }

            for (int i = 0; i < deleted.size(); i++) {
                String fileName = (String) deleted.get(i);
                fileMap.remove(fileName);
            }
        }

        if (crcMap.size()==0) {
            return true;
        }

        return false;
    }

    private static File[] getDataFilesInDir(File dir) {
        List resultList = new ArrayList();

        File[] list = dir.listFiles();

        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            if (file.isFile() && !file.isDirectory()) {
                resultList.add(file);
            }
        }
        return (File[]) resultList.toArray( new File[]{} );
    }

    private static File[] getSubDirs( File dir ) {
        List resultList = new ArrayList();

        File[] files = dir.listFiles();
        if (files!=null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file!=null&&file.isDirectory()) {
                    resultList.add(file);
                }
            }
        }

        return (File[]) resultList.toArray( new File[] {} );
    }

    private static boolean isFileIn(File file, File[] vetoFiles) {
        if ( vetoFiles == null ) { return false; }
        for (int i = 0; i < vetoFiles.length; i++) {
            File vetoFile = vetoFiles[i];
            if (vetoFile.equals(file)) {
                return true;
            }
        }
        return false;
    }

    private static  boolean hasDirSubDirs(File dir) {
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if ( file.isDirectory() ) {
                return true;
            }
        }

        return false;
    }

    private static  boolean hasDirDataFiles(File dir) {
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile() && !file.isDirectory()) {
                return true;
            }
        }

        return false;
    }

    public static File getArchiveRoot(File archivFile, String rootName) {
        return getArchiveRoot(archivFile, rootName, -1 );
    }

    private static  File getArchiveRoot(File archivFile, String rootName, int tiefe) {
        if ( tiefe==0 ) { return null; }

        File parentAtlasArchivDir = archivFile.getParentFile();

        if (parentAtlasArchivDir==null) { return null; }

        while (!rootName.equals(parentAtlasArchivDir.getName())) {

            if (tiefe>0) {
                parentAtlasArchivDir = getArchiveRoot( parentAtlasArchivDir, rootName, --tiefe );
            } else {
                parentAtlasArchivDir = parentAtlasArchivDir.getParentFile();
            }

        }
        return parentAtlasArchivDir;
    }

    public static InputStream getFileInputStream(String path) throws IOException {
        File file = new File(path);
        String fileName = file.getName();

        if (file.exists()) {
            return new FileInputStream(file);
        }

        List parentNames = new ArrayList();

        File parent = file.getParentFile();
        while (!parent.exists()) {
            parentNames.add(0, parent.getName());
            parent = parent.getParentFile();
        }

        File currentFile = new File( parent, parentNames.get(0) + ".zip" );
        if (!currentFile.exists()) { return null; }

        parentNames.remove( 0 );

        ZipFile zip = new ZipFile(currentFile);

        List tempFiles = new ArrayList();
        for (int i = 0; i < parentNames.size(); i++) {
            String currentName = (String) parentNames.get(i);
            ZipEntry currentEntry = zip.getEntry(currentName+".zip");

            File newFile = File.createTempFile("tmp_"+i, "" , parent );
            tempFiles.add( newFile );

            FileOutputStream fos = new FileOutputStream(newFile);

            InputStream inputStream = zip.getInputStream( currentEntry );
            BufferedInputStream bis = new BufferedInputStream(inputStream);

            int BUFFER = 4096;


            int count;

            byte data[] = new byte[BUFFER];
            while((count = bis.read(data, 0, BUFFER)) != -1) {
               fos.write(data, 0, count);
            }

            fos.close();
            zip.close();

            zip = new ZipFile(newFile);
        }

        ZipEntry entry = zip.getEntry( fileName );
        if (entry==null) {
            return null;
        }

        if (tempFiles.size()==0) {
            return zip.getInputStream( entry );
        }

        BufferedInputStream bis = new BufferedInputStream( zip.getInputStream( entry ) );

        File tempFile = File.createTempFile( "zip_"+System.currentTimeMillis() , "",  null );
        FileOutputStream fos = new FileOutputStream( tempFile );

        int count = 0;
        byte[] buffer = new byte[4096];
        while ( (count = bis.read(buffer, 0, 4096)) != -1 ) {
            fos.write( buffer );
        }
        bis.close();
        fos.close();

        for (int i = 0; i < tempFiles.size(); i++) {
            ((File) tempFiles.get(i)).delete();
        }

        return new ZipArchiverCleanedUpInputStream(tempFile);
    }

}

class ZipArchiverCleanedUpInputStream extends FileInputStream {
    File file;

    ZipArchiverCleanedUpInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    public void close() throws IOException {
        super.close();
        file.delete();
    }

}

