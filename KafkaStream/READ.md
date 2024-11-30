# Illustration d'un exemple d'utilisation de stream (API Kafka stream)

### Config
* La class <code>KafkaConfig</code> contient la configuration du stream et du producer qui produit des message à destination du stream
* La Class <code>Produce</code> produit des message (Chaque message est la concaténation d'un mois + deux jours de semaine)
* La Class <code>WordCountProcessor</code> définit la topologie du stream (Récupère un flux de donnée d'un topic en entrée, compte l'occurence des mots (En l'occurence mois et jours) et publie le résultat dans un topic en sortie)

### Démarrage de l'appli
Avant de démarrer l'application, il faut créer les deux topics (input-stream-topic, output-stream-topic)
Se placer dans le dossier bin/ de kafka et lancer les deux commandes
```
./kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --topic input-stream-topic 
./kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --topic output-stream-topic 
```

### Consommation des résultats du stream en temps réél 
```
./kafka-console-consumer --bootstrap-server localhost:9092 --topic output-stream-topic --property print.key=true --property print.value=true --property key.deseriializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer --from-beginning
```
Le résultat devrait resembler à ca
```
may	39
october	33
march	35
june	33
thursday	4
april	40
friday	4
saturday	3
november	25
monday	5
august	43
sunday	6
tuesday	1
january	39
wednesday	7
december	24
june	34
august	45
sunday	10
january	41
july	37
friday	10
thursday	9
september	36
tuesday	4
april	42
saturday	5
october	35
monday	11
wednesday	13
april	44
sunday	13
june	36
october	36
may	41
saturday	10
december	25
wednesday	16
november	28
thursday	15
monday	18
march	39
friday	14
tuesday	6
august	46
sunday	14
february	41
friday	16
monday	19
```

