import { useNavigate, useParams } from "react-router-dom";
import { useContext, useEffect } from "react";
import { useState } from "react";
import AppContext from "../Context/Context";
import axios from "../axios";

const ProductPage = () => {
  const { id } = useParams();
  const { addToCart, removeFromCart, refreshData } = useContext(AppContext);
  const [product, setProduct] = useState(null);
  const [imageUrl, setImageUrl] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/product/${id}`
        );
        setProduct(response.data);
        if (response.data.imageName) {
          fetchImage();
        }
      } catch (error) {
        console.error("Error fetching product:", error);
      }
    };

    const fetchImage = async () => {
      const response = await axios.get(
        `http://localhost:8080/api/product/${id}/image`,
        { responseType: "blob" }
      );
      setImageUrl(URL.createObjectURL(response.data));
    };

    fetchProduct();
  }, [id]);

  const deleteProduct = async () => {
    try {
      await axios.delete(`http://localhost:8080/api/product/${id}`);
      removeFromCart(id);
      console.log("Product deleted successfully");
      alert("Product deleted successfully");
      refreshData();
      navigate("/");
    } catch (error) {
      console.error("Error deleting product:", error);
    }
  };

  const handleEditClick = () => {
    navigate(`/product/update/${id}`);
  };

  const handlAddToCart = () => {
    addToCart(product);
    alert("Product added to cart");
  };
  if (!product) {
    return (
      <h2 className="text-center loading-text">
        Loading...
      </h2>
    );
  }
  return (
    <>
      <div className="containers">
        <img
          className="left-column-img"
          src={imageUrl}
          alt={product.imageName}
        />

        <div className="right-column">
          <div className="product-description">
            <div className="product-category-release">
              <span>{product.category}</span>
              <p className="release-date">
                <strong>Listed:</strong>{" "}
                <i>{new Date(product.releaseDate).toLocaleDateString()}</i>
              </p>
            </div>

            <h1 className="product-name">{product.name}</h1>
            <i className="product-brand">{product.brand}</i>
            
            <div className="product-price">
              <span>{"$" + product.price}</span>
              <h6>
                <strong>Stock Available:</strong>{" "}
                <i className="stock-quantity">{product.stockQuantity}</i>
              </h6>
            </div>
            
            <p className="product-description-title">
              PRODUCT DESCRIPTION:
            </p>
            <p>{product.description}</p>
          </div>

          <div className="update-button">
            <button
              className={`btn btn-primary cart-btn ${
                !product.productAvailable ? "disabled-btn" : ""
              }`}
              onClick={handlAddToCart}
              disabled={!product.productAvailable}
            >
              {product.productAvailable ? "Add to Cart" : "Out of Stock"}
            </button>
            <button
              className="btn btn-secondary"
              type="button"
              onClick={handleEditClick}
            >
              Update
            </button>
            <button
              className="btn btn-danger"
              type="button"
              onClick={deleteProduct}
            >
              Delete
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default ProductPage; 