package com.example.demo;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Scanner;

@SpringBootApplication
public class AzureProjeApplication {

	public static final String storageConnectionString = "UseDevelopmentStorage=true;";

	public static void main(String[] args) {
		SpringApplication.run(AzureProjeApplication.class, args);
		{
			File sourceFile = null;
			File downloadedFile = null;
			System.out.println("Azure Blob storage quick start sample");

			CloudStorageAccount storageAccount;
			CloudBlobClient blobClient = null;
			CloudBlobContainer container=null;

			try {
				// Parse the connection string and create a blob client to interact with Blob storage
				// BOB İLE BAĞLANTI OLUŞTURULUYOR
				storageAccount = CloudStorageAccount.parse(storageConnectionString);
				blobClient = storageAccount.createCloudBlobClient();
				container = blobClient.getContainerReference("quickstartcontainer");

				// Create the container if it does not exist with public access.
				//GENEL ERİŞİME SAHİP DEĞİLSE KAPSAYICI OLUŞTURULUYOR
				System.out.println("Creating container: " + container.getName());
				container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());

				//Creating a sample file
				sourceFile = File.createTempFile("sampleFile", ".txt");
				System.out.println("Creating a sample file at: " + sourceFile.toString());
				Writer output = new BufferedWriter(new FileWriter(sourceFile));
				output.write("Hello Azure!");
				output.close();

				//Getting a blob reference
				CloudBlockBlob blob = container.getBlockBlobReference(sourceFile.getName());

				//Creating blob and uploading file to it
				System.out.println("Uploading the sample file ");
				blob.uploadFromFile(sourceFile.getAbsolutePath());

				//Listing contents of container
				for (ListBlobItem blobItem : container.listBlobs()) {
					System.out.println("URI of blob is: " + blobItem.getUri());
				}

				// Download blob. In most cases, you would have to retrieve the reference
				// to cloudBlockBlob here. However, we created that reference earlier, and
				// haven't changed the blob we're interested in, so we can reuse it.
				// Here we are creating a new file to download to. Alternatively you can also pass in the path as a string into downloadToFile method: blob.downloadToFile("/path/to/new/file").
				downloadedFile = new File(sourceFile.getParentFile(), "downloadedFile.txt");
				blob.downloadToFile(downloadedFile.getAbsolutePath());
			}
			catch (StorageException ex)
			{
				System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage());
			}
			finally
			{
				System.out.println("The program has completed successfully.");
				System.out.println("Press the 'Enter' key while in the console to delete the sample files, example container, and exit the application.");

				//Pausing for input
				Scanner sc = new Scanner(System.in);
				sc.nextLine();

				System.out.println("Deleting the container");
				try {
					if(container != null)
						container.deleteIfExists();
				}
				catch (StorageException ex) {
					System.out.println(String.format("Service error. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
				}

				System.out.println("Deleting the source, and downloaded files");

				if(downloadedFile != null)
					downloadedFile.deleteOnExit();

				if(sourceFile != null)
					sourceFile.deleteOnExit();

				//Closing scanner
				sc.close();
			}
		}

	}

}
