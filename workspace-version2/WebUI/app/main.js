const app = new Vue({
    el: "#main",
    data: {
        customerSrvEndpoint: "http://localhost:8000",
        notificationSrvEndpoint: "http://localhost:8010",
        orderSrvEndpoint: "http://localhost:8030",
        productSrvEndpoint: "http://localhost:8040",
        products: [],
        categories: [],
        customers: [],
        orders: []
    },

    created() {

        // Retrieve all products
        axios.get(`${this.productSrvEndpoint}/products`).then(response => {
            response.data.forEach(product => {
                product.requestedAmount = 1;
            });
            this.products = response.data;
        });

        // Retrieve all product categories
        axios.get(`${this.productSrvEndpoint}/categories`).then(response => {
            this.categories = response.data;
        });

        // Retrieve all customers
        axios.get(`${this.customerSrvEndpoint}/customers`).then(response => {
            this.customers = response.data;
        });

        // Retrieve all orders
        axios.get(`${this.orderSrvEndpoint}/orders`).then(response => {
            this.orders = response.data;
        });

    },

    methods: {

        checkProductAvailability(product, index) {
            axios.get(`${this.productSrvEndpoint}/products/${product.id}/availability?amount=${product.requestedAmount}`).then(response => {
                product.availableAmount = response.data.availableAmount;
                this.products.splice(index, 1, product);
            });
        },

        checkCreditRating(customer, index) {
            axios.get(`${this.customerSrvEndpoint}/customers/${customer.id}/credit-rating-check`).then(response => {
                customer.rating = response.data.rating;
                this.customers.splice(index, 1, customer);
            });
        }
    }
});