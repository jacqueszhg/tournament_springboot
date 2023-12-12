/*Mais pas utiliser car h2 supporte pas les uuid*/
/*Pour permettre l'utilisation de la fonction uuid_generate_v4()*/
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

/*Créer la table team*/
CREATE TABLE public.team (
    name varchar NOT NULL,
    country varchar NOT NULL,
    id serial PRIMARY KEY
);

/*Pour un pays donnée on a que une équipe avec le même nom*/
ALTER TABLE
    public.team
ADD CONSTRAINT
    team_un
UNIQUE (country);


/*Créer la table tournament*/
CREATE TABLE public.tournament (
    title varchar NOT NULL,
    tournament_year int NOT NULL,
    id serial PRIMARY KEY
);

/*Il peut y avoir plusieur compétition dans une année
  mais avec des noms différents*/
ALTER TABLE
    public.tournament
ADD CONSTRAINT
    tournament_un
UNIQUE (tournament_year,title);

/*La table de liason entre team et tournament*/
/*Pour connaitre dans quel compétition participe les équipes*/
/*Pour connaitre les équipes participants aux tournois*/
CREATE TABLE public.participate (
    team_id int NOT NULL,
    tournament_id int NOT NULL,
    favorite int NOT NULL,
    id serial PRIMARY KEY
);
/*Une clé unique sur (tournouId, teamId),
  car l'inscription à un tournois se fait qu'une fois*/
ALTER TABLE
    public.participate
ADD CONSTRAINT
        participate_un
UNIQUE (tournament_id,team_id);

/*Clés étrangères*/
ALTER TABLE
    public.participate
ADD CONSTRAINT
    participate_to_team_fk FOREIGN KEY (team_id) REFERENCES public.team(id)
ON DELETE CASCADE;

ALTER TABLE
    public.participate
ADD CONSTRAINT
    participate_to_tournament_fk FOREIGN KEY (tournament_id) REFERENCES tournament (id)
ON DELETE CASCADE;

/*Fonction trigger qui permet de vérifier si la valeur du favorite est entre [0,5]*/
CREATE or replace  FUNCTION favorite_check_value() RETURNS trigger AS $favorite_check_value$
BEGIN

    IF NEW.favorite < 0 THEN
        RAISE EXCEPTION '% : cannot have a negative favorite', NEW.favorite;
END IF;

    IF NEW.favorite > 5 THEN
        RAISE EXCEPTION '% : cannot have a favorite greater than 5', NEW.favorite;
END IF;
return new;
END;
$favorite_check_value$ LANGUAGE plpgsql;


/*Trigger sur favorite qui doit être entre [0,5]*/
CREATE or replace TRIGGER favorite_check_value
    AFTER UPDATE or INSERT ON public.participate
    FOR EACH ROW
    EXECUTE FUNCTION favorite_check_value();
