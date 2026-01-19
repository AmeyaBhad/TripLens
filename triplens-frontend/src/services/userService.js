import axios from "axios";

const API_BASE_URL = "http://localhost:8080/users";

export const authenticate = (email, password) => {
  return axios.post(
    `${API_BASE_URL}/authenticate`,
    null,
    {
      params: {
        email,
        password
      }
    }
  );
};

export const addUser = (user) => {
  return axios.post(
    `${API_BASE_URL}/addUser`,
    null,
    {
      params: {
        name: user.name,
        email: user.email,
        password: user.password
      }
    }
  );
};
