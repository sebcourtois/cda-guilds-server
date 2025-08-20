# Projet Guildes



## Résumé

Simulation de caravanes marchandes

Plusieurs (6, comme c'est bizarre) nations lancent des expéditions commerciales
sous forme de caravanes maritimes ou terrestres, dans l'objectif de maximiser
leurs profits.



## Concepts abordés par ce projet

- Communication réseau connectée/non-connectée
- Architecture/Application Client/Serveur
- Architecture/Application répartie
- Programmation par contraintes
- Génération procédurale
- ...



## Description générale

Le monde est composé de régions contenant des comptoirs.
Chaque comptoir produit et consomme des ressources en fonction de la saison.


Les nations envoient, en fonction de leurs richesses, une (ou plusieurs) caravane(s)
qui passe(nt) de comptoir en comptoir et qui achètent/vendent des ressources.
Les caravanes doivent pouvoir passer de comptoir en comptoir et de région en région.
Elles consomment des ressources (nourriture, boisson, matériel) par jour
et/ou par événement.


Un comptoir a un droit d'accès fixe (par comptoir), un droit variable en
fonction de la taille de la caravane,  un droit variable en fonction de la cargaison.
Les transactions sont taxées en fonction du montant de la transaction et du
sens de la transaction (achat ou vente) et de la ressource.



## Description détaillée


### Les comptoirs

Les comptoirs sont des villes, des villages ou des cités, en fonction de leurs tailles.

Les gouvernements (comme tout gouvernement qui se respecte) taxent :
- les entrées & sorties dans le comptoir, en fonction de la taille de la caravane
  (personnel & moyens de transport)
- les transactions effectuées en leur sein, en fonction de la ressource,
  de la quantité et du sens de la transaction (Achat/Vente)

Un comptoir est caractérisé par :
- sa population (ou sa taille)
- sa position dans le monde (plat ou sphérique ?)
- le biome
- les ressources qu'elle produit
- les ressources qu'elle consomme
- les ordres d'achat qu'elle propose
- les ordres de vente qu'elle propose
-

### Les ordres d'achat et de vente

Les ordres sont caractérisés par :
 - la ressource
 - une quantité
 - un prix unitaire
 - une date de début (?)
 - une date de fin


### Les ressources

Les ressources sont réparties en catégories :
- Nourriture
- Richesses
- Matériel
- Matières premières
- ...

La production d'une ressource requiert une quantité de population en fonction :
- de la ressource
- du biome
- de la saison / du mois


### Les biomes

La liste des biomes sont :
* Terrestres
  * Arctiques & Subarctiques
    * Toundra
    * Taïga
  * Tempérés
    * Forêts tempérées
    * Forêts tempérées de conifères
    * Prairies & savanes tempérées
    * Forêts méditerranéennes
  * Tropicaux & subtropicaux
    * Forêts tropicales de conifères
    * Forêts tropicales humides
    * Forêts tropicales sèches
    * Prairies & savanes tropicales
    * Mangroves
    * Prairies & savanes inondées

* Maritimes
  * Eau douce
    * Ruisseaux, Rivières & Fleuves
    * Deltas
    * Mares, Etangs & Lacs
  * Eau de mer
    * Milieux polaires & Banquise
    * Mers intérieures
    * Récifs coralliens
    * Forêts de varech
    * Haute mer & Océan


### La caravane

La caravane est constituée de :
- de personnels
- de moyens de transport
- de ressources


### Le personnel

Le personnel peut avoir comme poste :
- Chef d'expédition : utilise Négociation
- Médecin : utilise Médecine
- Porteur : augmente la capacité de transport
- Garde : utilise Combat
- Guide / Navigateur : utilise Orientation
- Fourrageur : utilise Survie
- Cuisinier : utilise Cuisine
- Mage : utilise une des compétences de magie
- Artisan : utilise la compétence Artisanat

Un employé est décrite par :
- sa nation d'origine
- ses points de vie
- ses dégâts
- ses compétences
- son salaire par jour
- sa prime d'embauche (qui peut être égale à 0)
- son pourcentage sur les bénéfices de la caravane
- son moral


### Les moyens de transport

- Capacité en volume
- Capacité en masse
- Vitesse maximum
- Consommation par jour
- Ressource ou catégorie consommée
- Points de structure (aka points de vie)


### Les compétences

Les compétences sont à base de pourcentage.
Un personnage a *X* % dans une compétence.
Pour réaliser une action, un jet de compétence avec 1d100 doit être réalisé.
4 résultats sont possibles :
- Réussite critique : 1d100 <= min(10, max(1, *X* / 10))
- Réussite : 1d100 <= *X*
- Echec : 1d100 > *X*
- Echec critique : 1d100 > 89 + min(10, max(1, *X* / 10))

#### Négociation
- Réussite critique :  embauche l'unité au tarif normal - *CritSuccessNegociationBonus*
- Réussite : embauche l'unité au tarif normal
- Echec : embauche l'unité au tarif normal + *CritSuccessNegociationBonus*
- Echec critique : n'embauche pas l'unité et dépense *CritSuccessNegociationBonus*

#### Combat
- Réussite critique : *Damage* x *CritSuccessDamageMultiplier*
- Réussite : *Damage*
- Echec : aucun dégât
- Echec critique : l'attaque se blesse et perd *CritFailureDamageMultiplier* x *Damage*

#### Orientation
- Réussite critique : trouve un raccourci et réduit le voyage de *TravelOrientationDecrease* jours
- Réussite : aucun changement
- Echec : se perd et rallonge le voyage de *TravelOrientationIncrease* jours
- Echec critique : se perd et rallonge le voyage de 2 x *TravelOrientationIncrease* jours

#### Magie noire
#### Magie rouge
#### Magie invisible
#### Magie jaune
#### Magie violet
#### Magie vert
- Réussite critique : coût en loom divisé par 2, effet normal
- Réussite : coût en loom normal, effet normal
- Echec : coût en loom normal, effet nul
- Echec critique : coût en loom normal, effet nul, perte de compétence

#### Médecine
- Réussite critique : soigne 2 x *HitPointsCure*
- Réussite : soigne 2 x *HitPointsCure*
- Echec : aucun effet
- Echec critique : blesse le patient 0.5 x *HitPointsCure*

#### Survie
- Réussite critique : génère *SurvivalFoodMultiplier* x *SurvivalFood* nourritures/eau
- Réussite : génère *SurvivalFood* nourritures/eau
- Echec : ne génère rien
- Echec critique : intoxication, chaque créature perd *SurvivalIntoxDamage* PV

#### Cuisine
- Réussite critique : remonte le moral de *CritSuccessCookMultiplier* x *CookingMoralIncrease*
- Réussite : remonte le moral de *CookingMoralIncrease*
- Echec : aucun effet
- Echec critique : rebellion

#### Artisanat
- Réussite critique : répare 2 x *HitPointsRepair*
- Réussite : répare 2 x *HitPointsRepair*
- Echec : aucun effet
- Echec critique : endommage le moyen de transport de 0.5 x *HitPointsRepair*


### Les nations

#### Ashragor
**Couleur du loom :** noir

**Effet :** *"recrute"* & *"paye"* des squelettes en loom noir.

1 squelette coûte *BlackLoomSkeletonInvocationCost* points de loom à recruter puis *BlackLoomSkeletonMaintenanceCost* points par jour.

1 squelette peut porter *SkeletonCarriedVolume* m3 pour un poids total de *SkeletonCarriedMass* kg


#### Felsin
**Couleur du loom :** invisible

**Effet :** bonus aux dégâts en utilisant du loom invisible.

Un guerrier Felsin génère *InvisibleLoomDayRecovery* points par jour et peut stocker au maximum
*InvisibleLoomCapacity* points de loom invisible.

1 point de loom invisible donne un bonus de *InvisibleLoomDamageBonus* aux dégâts.


#### Gehemdal
**Couleur du loom :** rouge

**Effet :** bonus aux points de vie en utilisant du loom rouge.

1 point de loom rouge donne *RedLoomHitPointsMaximumBonus* points de vie.

#### Kheyza
**Couleur du loom :** violet

**Effet :** donne un bonus de déplacement terrestre.

1 point de loom violet utilisé par élément de la caravane augmente la vitesse
terrestre de *PurpleLoomLandTravelIncrease* km/h.

#### Ulmeq
**Couleur du loom :** vert

**Effet :** permet de connaitre la demande/prix d'une ressource à distance.

1 point de loom vert permet d'avoir les informations de vente/achat d'une ressource
dans un rayon de *GreenLoomInformationRange* km.

#### Venn'dys
**Couleur du loom :** jaune

**Effet :** donne un bonus de déplacement terrestre.

1 point de loom jaune utilisé par navire augmente la vitesse
maritime de *YellowLoomSeaTravelIncrease* km/h.



## Description technique

Chaque stagiaire développe un client ET un serveur.

Un serveur représente une région contenant des comptoirs.
Un client représente une nation qui envoie une (ou plusieurs) caravane(s).
