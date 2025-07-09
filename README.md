# 🔧 Ferramenta para Aquisição e Visualização de Dados Ambientais de Estacoes Meteo-oceanográficas

A ferramenta foi desenvolvida para facilitar o acesso aos dados de todos os projetos e estações meteo-oceanográficas ligados ao Global Tropical Moored Buoy Array disponível no site do PMEL (https://www.pmel.noaa.gov/), além de possibilitar a criação de gráficos interativos e dinâmicos que atendam da melhor forma a necessidade dos usuários do sistema de forma **gratuita**.

Esse projeto foi construído em cima da licença de código aberto (*open-source*) do <a target="_blank" rel="noopener noreferrer" href='https://github.com/MaryeneClaudino/Ferramenta-para-Aquisicao-e-Visualiza-o-de-Dados-Ambientais-de-Estacoes-Meteo-oceanograficas/blob/main/LICENSE'>MIT</a>

---

## 🚀 Implantação

Para o correto funcionamento da ferramenta é fundamental ter o PostgreSQL já instalado no ambiente. Para sua instalação consulte a documentação em: 
  
``` bash
https://www.postgresql.org/download/
```
    
Também faz-se necessário a instalação do docker, para tal, consulte a documentação em:
    
``` bash
https://docs.docker.com/
```

### 1. 🔁 Clone o Repositório

```bash
git clone https://github.com/MaryeneClaudino/Ferramenta-para-Aquisicao-e-Visualiza-o-de-Dados-Ambientais-de-Estacoes-Meteo-oceanograficas.git
cd Ferramenta-para-Aquisicao-e-Visualiza-o-de-Dados-Ambientais-de-Estacoes-Meteo-oceanograficas
```

### 2. ⚙️ Configuração de Variáveis de Ambiente

É **imprescindível** configurar as variáveis de ambiente **ANTES** de iniciar uma aquisição de dados ou as rotas da API.

- Arquivos de configuração:
  - Backend: [`backend/example.env`](backend/example.env)
  - Aquisição e Importação: [`acquirer_and_importer/example.env`](acquirer_and_importer/example.env)

> 📄 Para entender o significado e uso de cada variável, consulte o arquivo [variaveis.md](./variaveis.md)

---

## ▶️ Executando o Sistema

Após configurar o ambiente e variáveis:

# Backend

``` bash
# Instalador do Sistema
cd backend
mvn spring-boot:run
```

``` bash
# Aquisição e Importação de Dados de uma Estação Meteo-oceanogáfica
cd acquirer_and_importer
java -jar acquirer_and_importer.jar <nome da estação> <all(série temporal completa) ou new(dados recentes)>
```

# Frontend

``` bash
# Iniciar container Grafana
cd grafana_docker_compose
sudo docker compose up -d
```

``` bash
# Finalizar container Grafana
cd grafana_docker_compose
sudo docker compose down
```

O sistema estará disponível em:

- Frontend: [http://localhost:3000](http://localhost:3000)
- Backend (Swagger): [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 📝 Licença

Este projeto está sob a licença MIT.

---

📌 Desenvolvido por [Maryene Claudino](https://github.com/MaryeneClaudino)
