# PSS

This is a proof-of-concept implementation of 
[Private Searching On Streaming Data](https://www.iacr.org/archive/crypto2005/36210217/36210217.pdf)

##How to use

The codes have depends on [Scapi](https://github.com/cryptobiu/scapi)
To run the code, the keyword universe should be stored in `data/keywords/word_list`, and targetted kerword in `data/keywords/target`. And the program will recursively retrieve everything in the `data/emails/` directory.

To run the program, follow the steps:

1. [Install scapi](http://scapi.readthedocs.io/en/latest/)
2. under this directory, run `scapic *.java`
3. `scapi PSS`

The program will output timer to standard output and the documents retrieved to standard error.

##Performance

On a 2.9GHz i5 cpu, the setup phase will run for around 6 minutes with a keyword universe of size 1000. And the time grows linearly with the size of the universe.

The online filtering phase will take 250ms per document, the actual time is related to the size of the document. 

The decryption phase with the default buffer size (50\*30) will run around 30 minutes, and the time increase linearly with the
buffer size (both parameters).

##Implementation Details

The protocol is mainly implemented in `EncryptedProg.java`, from `HEKey.java`, `HEPlainText.java`, `HECipherText.java` it imports all the functionality of additive homomorphic encryption. And these three classes uses the encryption algorithm in Scapi. And `PSS.java` does the job of manage the files and initialize the protocol instance.

The `PSFile.java` and `PSPack.java` handles file validation and packing. They will make a string into one (or many if it's too large for one) packets, starting with a random file id and the position of the packet (from the original string), a fix length payload (padded if necessary), and a 64 bit hash value at the end.

##Work that's not done
- If it were to be used over the network, the objects need to implement `load` and `dump` methods to serialize the correct properties (not the ones that should be kept secret).
- The key size and packet size are currently hard-coded
- Though the packets are retrieved from the buffer, it's not reassembled.
- It doesn't support non-ASCII files 
