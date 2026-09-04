--
-- PostgreSQL database dump
--

\restrict P8KuyUSNJIYysSKQj2b1hTO6QPhMiZDMMCDCw3N9MAQlHtX6PXnIS2hDBkcl7JY

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: graduado; Type: TABLE; Schema: public; Owner: pid_issuer
--

CREATE TABLE public.graduado (
    id bigint NOT NULL,
    usuario_autenticacion character varying(150) NOT NULL,
    nombres character varying(200) NOT NULL,
    apellidos character varying(200) NOT NULL,
    tipo_identificacion character varying(3) NOT NULL,
    numero_identificacion character varying(50),
    programa_id bigint NOT NULL,
    titulo_otorgado character varying(300) NOT NULL,
    fecha_ingreso date,
    fecha_de_grado date NOT NULL,
    promedio_acumulado character varying(30),
    creditos_aprobados integer,
    semestres_cursados integer,
    estado_academico character varying(30) DEFAULT 'GRADUADO'::character varying NOT NULL,
    CONSTRAINT ck_graduado_tipo CHECK (((tipo_identificacion)::text = ANY ((ARRAY['CC'::character varying, 'CE'::character varying, 'TI'::character varying, 'PAS'::character varying, 'PPT'::character varying, 'PEP'::character varying])::text[])))
);


ALTER TABLE public.graduado OWNER TO pid_issuer;

--
-- Name: graduado_id_seq; Type: SEQUENCE; Schema: public; Owner: pid_issuer
--

CREATE SEQUENCE public.graduado_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.graduado_id_seq OWNER TO pid_issuer;

--
-- Name: graduado_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pid_issuer
--

ALTER SEQUENCE public.graduado_id_seq OWNED BY public.graduado.id;


--
-- Name: issued_credential; Type: TABLE; Schema: public; Owner: pid_issuer
--

CREATE TABLE public.issued_credential (
    id bigint NOT NULL,
    credential_format character varying(255) NOT NULL,
    credential_type character varying(255) NOT NULL,
    issued_at timestamp with time zone NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    notification_id character varying(255),
    status_list_uri character varying(2048),
    status_list_index bigint,
    client_status_list_uri character varying(2048) NOT NULL,
    client_status_list_index bigint NOT NULL,
    key_storage_status_list_uri character varying(2048),
    key_storage_status_list_index bigint,
    credential_identifier uuid NOT NULL
);


ALTER TABLE public.issued_credential OWNER TO pid_issuer;

--
-- Name: issued_credential_id_seq; Type: SEQUENCE; Schema: public; Owner: pid_issuer
--

CREATE SEQUENCE public.issued_credential_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.issued_credential_id_seq OWNER TO pid_issuer;

--
-- Name: issued_credential_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pid_issuer
--

ALTER SEQUENCE public.issued_credential_id_seq OWNED BY public.issued_credential.id;


--
-- Name: programa_academico; Type: TABLE; Schema: public; Owner: pid_issuer
--

CREATE TABLE public.programa_academico (
    id bigint NOT NULL,
    codigo character varying(50) NOT NULL,
    nombre character varying(300) NOT NULL,
    nivel_academico character varying(30) NOT NULL,
    modalidad character varying(30) NOT NULL,
    facultad character varying(300),
    unidad_academica character varying(300),
    titulo_otorgado character varying(300) NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_programa_modalidad CHECK (((modalidad)::text = ANY ((ARRAY['PRESENCIAL'::character varying, 'DISTANCIA'::character varying, 'VIRTUAL'::character varying, 'HIBRIDA'::character varying])::text[]))),
    CONSTRAINT ck_programa_nivel CHECK (((nivel_academico)::text = ANY ((ARRAY['TECNICO'::character varying, 'TECNOLOGICO'::character varying, 'PREGRADO'::character varying, 'ESPECIALIZACION'::character varying, 'MAESTRIA'::character varying, 'DOCTORADO'::character varying])::text[])))
);


ALTER TABLE public.programa_academico OWNER TO pid_issuer;

--
-- Name: programa_academico_id_seq; Type: SEQUENCE; Schema: public; Owner: pid_issuer
--

CREATE SEQUENCE public.programa_academico_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.programa_academico_id_seq OWNER TO pid_issuer;

--
-- Name: programa_academico_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pid_issuer
--

ALTER SEQUENCE public.programa_academico_id_seq OWNED BY public.programa_academico.id;


--
-- Name: graduado id; Type: DEFAULT; Schema: public; Owner: pid_issuer
--

ALTER TABLE ONLY public.graduado ALTER COLUMN id SET DEFAULT nextval('public.graduado_id_seq'::regclass);


--
-- Name: issued_credential id; Type: DEFAULT; Schema: public; Owner: pid_issuer
--

ALTER TABLE ONLY public.issued_credential ALTER COLUMN id SET DEFAULT nextval('public.issued_credential_id_seq'::regclass);


--
-- Name: programa_academico id; Type: DEFAULT; Schema: public; Owner: pid_issuer
--

ALTER TABLE ONLY public.programa_academico ALTER COLUMN id SET DEFAULT nextval('public.programa_academico_id_seq'::regclass);


--
-- Data for Name: graduado; Type: TABLE DATA; Schema: public; Owner: pid_issuer
--

COPY public.graduado (id, usuario_autenticacion, nombres, apellidos, tipo_identificacion, numero_identificacion, programa_id, titulo_otorgado, fecha_ingreso, fecha_de_grado, promedio_acumulado, creditos_aprobados, semestres_cursados, estado_academico) FROM stdin;
1	mlopez	María	López	CC	87654321	1	Especialista	2023-02-01	2024-12-15	4.8	45	2	GRADUADO
2	jperez	Juan	Pérez	CC	12345678	2	Ingeniero de Sistemas	2018-08-20	2024-05-30	4.2	160	10	GRADUADO
3	cgomez	Carlos	Gómez	CC	98765432	3	Magíster	2022-01-10	2025-03-20	4.6	60	4	GRADUADO
4	arodriguez	Ana	Rodríguez	CC	37795689	4	Doctor	2020-09-01	2026-07-15	4.9	120	12	GRADUADO
\.


--
-- Data for Name: issued_credential; Type: TABLE DATA; Schema: public; Owner: pid_issuer
--

COPY public.issued_credential (id, credential_format, credential_type, issued_at, expires_at, notification_id, status_list_uri, status_list_index, client_status_list_uri, client_status_list_index, key_storage_status_list_uri, key_storage_status_list_index, credential_identifier) FROM stdin;
\.


--
-- Data for Name: programa_academico; Type: TABLE DATA; Schema: public; Owner: pid_issuer
--

COPY public.programa_academico (id, codigo, nombre, nivel_academico, modalidad, facultad, unidad_academica, titulo_otorgado, activo, fecha_creacion) FROM stdin;
1	ESP-001	Especialización en Desarrollo de Software	ESPECIALIZACION	VIRTUAL	Facultad de Ingenierías Fisico Mecanicas	Escuela de Ingeniería de Sistemas	Especialista	t	2026-09-02 16:41:12.194546+00
2	PRE-001	Ingeniería de Sistemas	PREGRADO	PRESENCIAL	Facultad de Ingenierías Fisico Mecanicas	Escuela de Ingeniería de Sistemas	Ingeniero de Sistemas	t	2026-09-02 16:41:12.194546+00
3	MAE-001	Maestría en Ingeniería de Sistemas e Informática	MAESTRIA	PRESENCIAL	Facultad de Ingenierías Fisico Mecanicas	Escuela de Ingeniería de Sistemas	Magíster	t	2026-09-02 16:41:12.194546+00
4	DOC-001	Doctorado en Ciencias de la Computación	DOCTORADO	PRESENCIAL	Facultad de Ingenierías Fisico Mecanicas	Escuela de Ingeniería de Sistemas	Doctor	t	2026-09-02 16:41:12.194546+00
\.


--
-- Name: graduado_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pid_issuer
--

SELECT pg_catalog.setval('public.graduado_id_seq', 4, true);


--
-- Name: issued_credential_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pid_issuer
--

SELECT pg_catalog.setval('public.issued_credential_id_seq', 1, false);


--
-- Name: programa_academico_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pid_issuer
--

SELECT pg_catalog.setval('public.programa_academico_id_seq', 4, true);


--
-- Name: graduado graduado_pkey; Type: CONSTRAINT; Schema: public; Owner: pid_issuer
--

ALTER TABLE ONLY public.graduado
    ADD CONSTRAINT graduado_pkey PRIMARY KEY (id);


--
-- Name: issued_credential issued_credential_pkey; Type: CONSTRAINT; Schema: public; Owner: pid_issuer
--

ALTER TABLE ONLY public.issued_credential
    ADD CONSTRAINT issued_credential_pkey PRIMARY KEY (id);


--
-- Name: programa_academico programa_academico_codigo_key; Type: CONSTRAINT; Schema: public; Owner: pid_issuer
--

ALTER TABLE ONLY public.programa_academico
    ADD CONSTRAINT programa_academico_codigo_key UNIQUE (codigo);


--
-- Name: programa_academico programa_academico_pkey; Type: CONSTRAINT; Schema: public; Owner: pid_issuer
--

ALTER TABLE ONLY public.programa_academico
    ADD CONSTRAINT programa_academico_pkey PRIMARY KEY (id);


--
-- Name: idx_issued_credential_expires_at; Type: INDEX; Schema: public; Owner: pid_issuer
--

CREATE INDEX idx_issued_credential_expires_at ON public.issued_credential USING btree (expires_at);


--
-- Name: idx_issued_credential_notification_id; Type: INDEX; Schema: public; Owner: pid_issuer
--

CREATE INDEX idx_issued_credential_notification_id ON public.issued_credential USING btree (notification_id);


--
-- Name: idx_issued_credential_uuid; Type: INDEX; Schema: public; Owner: pid_issuer
--

CREATE UNIQUE INDEX idx_issued_credential_uuid ON public.issued_credential USING btree (credential_identifier);


--
-- Name: idx_programa_modalidad; Type: INDEX; Schema: public; Owner: pid_issuer
--

CREATE INDEX idx_programa_modalidad ON public.programa_academico USING btree (modalidad);


--
-- Name: idx_programa_nivel; Type: INDEX; Schema: public; Owner: pid_issuer
--

CREATE INDEX idx_programa_nivel ON public.programa_academico USING btree (nivel_academico);


--
-- Name: graduado fk_graduado_programa; Type: FK CONSTRAINT; Schema: public; Owner: pid_issuer
--

ALTER TABLE ONLY public.graduado
    ADD CONSTRAINT fk_graduado_programa FOREIGN KEY (programa_id) REFERENCES public.programa_academico(id);


--
-- PostgreSQL database dump complete
--

\unrestrict P8KuyUSNJIYysSKQj2b1hTO6QPhMiZDMMCDCw3N9MAQlHtX6PXnIS2hDBkcl7JY

