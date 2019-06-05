Vue.prototype.$patternVersion = false;

Vue.component("test-results", {
    props: ["test"],
    template: "#results-component",
    methods: {
        hasFailed(results) {
            return results.map(result => result.successful.expected === result.successful.achieved).includes(false);
        }
    }
});

const startPage = Vue.component("start-page", {
    template: "#startpage",
    created() {
        console.log(`Version ${this.$patternVersion ? "2" : "1"} of the system is tested!`);
    }
});

const exercise01 = Vue.component("exercise01", {
    template: "#exercise01",
    data: function () {
        return {
            test: {
                results: [],
                idKey: "id",
                message: "",
                title: "End-2-End Order Check Results",
                validationFinished: false
            },
            notificationSrvEndpoint: "http://localhost:8010/marketing-mails?limit=4000",
            orderSrvEndpoint: this.$patternVersion ? "http://localhost:8020/order-process" : "http://localhost:8030/orders"
        }
    },

    methods: {
        startValidation() {
            // test order proccess adjustments end-2-end
            axios.get("app/data/order-checks.json").then(response => {
                const testSpecs = response.data;
                this.test.validationFinished = false;
                this.test.message = "Validation started...";
                const promises = [];
                testSpecs.forEach(check => {
                    promises.push(axios.post(this.orderSrvEndpoint, check.order).catch(e => e));
                });
                Promise.all(promises).then(results => {
                    results.forEach((result, index) => {
                        if (result.response && result.response.status > 380) {
                            // http error
                            testSpecs[index].successful.achieved = false;
                        } else {
                            // http success, further check for marketing mail
                            const orderId = parseInt(result.data.message.substring(result.data.message.indexOf("ID ") + 3, result.data.message.indexOf(" success")));
                            axios.get(this.notificationSrvEndpoint).then(result => {
                                result.data = result.data || [];
                                testSpecs[index].successful.achieved = result.data.find(mail => mail.order.id === orderId) !== undefined;
                            });
                        }
                    });
                    this.test.results = testSpecs;
                    this.test.message = "Validation finished!";
                    this.test.validationFinished = true;
                });
            });
        }
    }
});

const exercise02 = Vue.component("exercise02", {
    template: "#exercise02",
    data: function () {
        return {
            test: {
                results: [],
                idKey: "id",
                message: "",
                title: "Decomposition Check Results",
                validationFinished: false
            }
        }
    },

    methods: {
        startValidation() {
            // test service decomposition end-2-end
            axios.get("app/data/decomposition-checks.json").then(response => {
                const testSpecs = response.data;
                this.test.validationFinished = false;
                this.test.message = "Validation started...";
                const promises = [];
                testSpecs.forEach(check => {
                    if (check.config.url === "order") {
                        check.config.url = this.$patternVersion ? "http://localhost:8020/order-process" : "http://localhost:8030/orders";
                    }
                    promises.push(axios(check.config).catch(e => e));
                });
                Promise.all(promises).then(results => {
                    results.forEach((result, index) => {
                        if ((result.response && result.response.status > 380) || result.data === undefined) {
                            // http error
                            testSpecs[index].successful.achieved = false;
                        } else {
                            // http success
                            testSpecs[index].successful.achieved = true;
                        }
                    });
                    this.test.results = testSpecs;
                    this.test.message = "Validation finished!";
                    this.test.validationFinished = true;
                });
            });
        }
    }
});

const exercise03 = Vue.component("exercise03", {
    template: "#exercise03",
    data: function () {
        return {
            test: {
                results: [],
                idKey: "id",
                message: "",
                title: "New Product Notification Check Results",
                validationFinished: false
            },
            notificationSrvEndpoint: "http://localhost:8010",
            warehouseSrvEndpoint: "http://localhost:8070"
        }
    },

    methods: {
        startValidation() {
            // test service decomposition end-2-end
            axios.get("app/data/new-product-checks.json").then(response => {
                const testSpecs = response.data;
                this.test.validationFinished = false;
                this.test.message = "Validation started...";
                const promises = [];
                testSpecs.forEach(check => {
                    promises.push(axios(check.config).catch(e => e));
                });
                Promise.all(promises).then(results => {
                    results.forEach((result, index) => {
                        if ((result.response && result.response.status > 380) || result.data === undefined) {
                            // http error
                            testSpecs[index].successful.achieved = false;
                        } else {
                            // http success
                            const newProductId = parseInt(result.data.message.substring(result.data.message.indexOf("ID ") + 3, result.data.message.indexOf(" succ")));

                            Promise.all([
                                axios.get(`${this.notificationSrvEndpoint}/new-products/${newProductId}`).catch(e => e),
                                axios.get(`${this.notificationSrvEndpoint}/product-mails?limit=4000`).catch(e => e),
                                axios.get(`${this.warehouseSrvEndpoint}/products/${newProductId}/availability?amount=8`).catch(e => e)
                            ]).then(results2 => {
                                if (
                                    // new product has been added
                                    results2[0].data && results2[0].data.id === newProductId &&
                                    // new product mail has been sent
                                    this.findProductMailRequest(results2[1].data, newProductId) &&
                                    // available amount for new product has been ordered
                                    this.checkAvailabilityResponse(results2[2].data)
                                ) {
                                    testSpecs[index].successful.achieved = true;
                                } else {
                                    testSpecs[index].successful.achieved = false;
                                }
                            });
                        }
                    });
                    this.test.results = testSpecs;
                    this.test.message = "Validation finished!";
                    this.test.validationFinished = true;
                });
            });
        },

        findProductMailRequest(data, newProductId) {
            return data.find(mail => {
                return mail.product.id === newProductId;
            }) !== undefined;
        },

        checkAvailabilityResponse(data) {
            if (data === undefined) {
                return false;
            }

            if (this.$patternVersion) {
                return data.availableAmount === 10;
            } else {
                return data.available;
            }
        }
    }
});

const routes = [{
        path: "/",
        component: startPage
    },
    {
        path: "/exercise01",
        component: exercise01
    },
    {
        path: "/exercise02",
        component: exercise02
    },
    {
        path: "/exercise03",
        component: exercise03
    }
];

const router = new VueRouter({
    routes
});

const app = new Vue({
    el: "#main",
    router
});